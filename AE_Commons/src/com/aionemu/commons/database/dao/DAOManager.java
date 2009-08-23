/*
 * This file is part of aion-emu.
 *
 * aion-emu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-emu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aionemu.commons.database.dao;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.aionemu.commons.callbacks.EnhancedObject;
import com.aionemu.commons.database.DatabaseConfig;
import com.aionemu.commons.database.DatabaseInfo;
import com.aionemu.commons.database.dao.cache.Cache;
import com.aionemu.commons.database.dao.cache.CacheManager;
import com.aionemu.commons.database.dao.scriptloader.DAOLoader;
import com.aionemu.commons.scripting.ScriptContext;
import com.aionemu.commons.scripting.scriptmanager.ScriptContextCreationListener;
import com.aionemu.commons.scripting.scriptmanager.ScriptManager;
import com.aionemu.commons.scripting.scriptmanager.ScriptManagerReloadListener;
import com.aionemu.commons.services.ScriptService;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Binder;

/**
 * This class manages {@link DAO} implementations, it resolves valid implementation for current database.<br>
 * In the end of use {@link #shutdown()} method should be called.
 *
 * @author SoulKeeper
 */
public class DAOManager {

	/**
	 * Logger for DAOManager class
	 */
	private static final Logger log = Logger.getLogger(DAOManager.class);

	/**
	 * Collection of registered DAOs
	 */
	private final Map<Class<? extends DAO>, DAO> daoMap = new HashMap<Class<? extends DAO>, DAO>();

	/**
	 * Cache for DAO's
	 */
	private final CacheManager cacheManager = new CacheManager();

	/**
	 * Information about used database
	 */
	private final DatabaseInfo databaseInfo;

	/**
	 * DatabaseConfig object reference
	 */
	private final DatabaseConfig databaseConfig;

	/**
	 * Parent injection module for guice
	 */
	private final Injector injector;

	/**
	 * ScriptService that will be used to load DAOs
	 */
	private ScriptService scriptService;

	/**
	 * Creates new DAOManager
	 *
	 * @param injector Guice injection modules
	 * @param databaseInfo   information about database that is used
	 * @param databaseConfig database config that is used
	 * @param scriptService  scripting service to use, usually it's a sigleton
	 */
	public DAOManager(Injector injector, DatabaseInfo databaseInfo, DatabaseConfig databaseConfig, ScriptService scriptService) {
		this.injector = injector;
		this.scriptService = scriptService;
		this.databaseInfo = databaseInfo;
		this.databaseConfig = databaseConfig;

		initDAOs();
		injectDependecies();
	}

	/**
	 * Initializes DAOs
	 */
	protected void initDAOs() {
		ScriptManager sm = new ScriptManager();
		EnhancedObject eo = (EnhancedObject) sm;
		eo.addCallback(new ScriptContextCreationListener() {
			@Override
			protected void contextCreated(ScriptContext context) {
				if (context.getParentScriptContext() == null) {
					context.setClassListener(new DAOLoader(DAOManager.this));
				}
			}
		});
		try {
			sm.load(databaseConfig.getScriptContextDescriptor());
		} catch (Exception e) {
			throw new RuntimeException("Can't initialize database factory", e);
		}

		eo.addCallback(new ScriptManagerReloadListener() {

			@Override
			public void beforeReload() {
				// Do nothing
			}

			@Override
			public void afterReload() {
				// dependencies has to be reinjected
				injectDependecies();
			}
		});

		scriptService.addScriptManager(sm, databaseConfig.getScriptContextDescriptor());
	}

	/**
	 * Injecting dependencies to DAO's using Guice.<br>
	 * It's done by creating child injector. It inherrits all parent's injections + we add bindings of daos.<br>
	 * Child injector is not stored anywhere, so it should be garbage collected.<br>
	 * Such approach gives us easy way to reload DAOs using common approach via ScriptManager/ScriptService
	 */
	protected void injectDependecies() {

		Injector childInjector = injector.createChildInjector(new Module(){

			@SuppressWarnings({"unchecked"})
			@Override
			public void configure(Binder binder) {
				for(Class c : daoMap.keySet()){
					binder.bind(c).toInstance(getDAO(c));
				}
			}
		});

		for (DAO dao : daoMap.values()) {
			childInjector.injectMembers(dao);
		}
	}

	/**
	 * Should be called in case of shutdown of DAOManager
	 */
	public void shutdown() {
		ScriptService ss;
		synchronized (this) {
			ss = scriptService;
			scriptService = null;
		}

		if (ss == null) {
			log.error("Attempt to shutdown DAOManager for two times?");
			return;
		}

		ss.unload(databaseConfig.getScriptContextDescriptor());
	}

	/**
	 * Returns DAO implementation by DAO class. Typical usage:
	 * <p/>
	 * <pre>
	 * AccountDAO	dao	= DAOManager.getDAO(AccountDAO.class);
	 * </pre>
	 *
	 * @param clazz Base DAO class implementation of which was registered
	 * @param <T>   Subclass of DAO
	 * @return DAO implementation
	 * @throws DAONotFoundException if DAO implementation not found
	 */
	@SuppressWarnings("unchecked")
	public <T extends DAO> T getDAO(Class<T> clazz) throws DAONotFoundException {
		DAO result = daoMap.get(clazz);

		if (result == null) {
			String s = "DAO for class " + clazz.getName() + " not implemented";
			log.error(s);
			throw new DAONotFoundException(s);
		}

		return (T) result;
	}

	/**
	 * Registers {@link DAO}.<br>
	 * First it checks if current DB is supported by calling {@link DAOUtils#isDBSupportedByDAO(Class, DatabaseInfo)}, after that method
	 * ckecks if another DAO isn't in use already. If not - dao is successfully registered.
	 *
	 * @param clazz DAO implementation
	 * @throws DAOAlreadyRegisteredException if DAO is already registered
	 * @throws IllegalAccessException		if something went wrong during instantiation of DAO
	 * @throws InstantiationException		if something went wrong during instantiation of DAO
	 */
	public void registerDAO(Class<? extends DAO> clazz) throws DAOAlreadyRegisteredException,
			IllegalAccessException, InstantiationException {
		if (!DAOUtils.isDBSupportedByDAO(clazz, databaseInfo)) {
			log.debug("DAO " + clazz.getName() + " skipped, " + databaseInfo + " is not supported.");
			return;
		}

		Class<? extends DAO> baseClass = DAOUtils.getBaseClass(clazz);

		synchronized (this) {
			DAO oldDao = daoMap.get(baseClass);
			if (oldDao != null) {
				StringBuilder sb = new StringBuilder();
				sb.append("DAO with className ").append(baseClass).append(" is used by ");
				sb.append(oldDao.getClass().getName()).append(". Can't override with ");
				sb.append(clazz.getName()).append(".");
				String s = sb.toString();
				log.error(s);
				throw new DAOAlreadyRegisteredException(s);
			} else {
				if(DAOUtils.isCacheable(clazz)){
					daoMap.put(baseClass, cacheManager.wrap(clazz.newInstance()));
				} else {
					daoMap.put(baseClass, clazz.newInstance());
				}
				log.info("Using DAO " + clazz.getName() + " for " + databaseInfo);
			}
		}
	}

	/**
	 * Unregisters DAO class
	 *
	 * @param daoClass DAO implementation to unregister
	 */
	@SuppressWarnings({"unchecked"})
	public void unregisterDAO(Class<? extends DAO> daoClass) {
		Class<? extends DAO> baseClass = DAOUtils.getBaseClass(daoClass);

		synchronized (this) {
			for (DAO dao : daoMap.values()) {
				Class clazz;
				if(dao instanceof Cache){
					clazz = ((Cache)dao).getDAO().getClass();
				} else {
					clazz = dao.getClass();
				}
				if (clazz == daoClass) {
					daoMap.remove(baseClass);
					if(dao instanceof Cache){
						cacheManager.removeWrapping(daoClass);
					}
					break;
				}
			}
		}
	}

	/**
	 * Just a check to ensure correct shutdown on DAO Manager
	 *
	 * @throws Throwable
	 */
	@Override
	protected void finalize() throws Throwable {
		if (scriptService != null) {
			log.error("DAOManager was garbage collected, but shutdown wasn't called. Forcing shutdown.");
			shutdown();
		}
		super.finalize();
	}
}
