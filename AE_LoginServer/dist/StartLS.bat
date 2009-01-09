TITLE aion-emu - Login Server
@echo off
java -Xms8m -Xmx32m -da -dsa -Xbootclasspath/p:./libs/jsr166.jar -cp ./libs/*;ae_login.jar aionemu.LoginServer