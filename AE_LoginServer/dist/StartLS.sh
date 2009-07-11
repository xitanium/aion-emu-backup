#!/bin/sh
java -Xms8m -Xmx32m -ea -Xbootclasspath/p:./lib/jsr166.jar -javaagent:lib/ae_commons.jar -cp ./libs/*:ae_login.jar com.aionemu.loginserver.LoginServer
