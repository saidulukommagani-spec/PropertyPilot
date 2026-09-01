@ECHO OFF
SETLOCAL

SET "BASE_DIR=%~dp0"
SET "WRAPPER_JAR=%BASE_DIR%.mvn\wrapper\maven-wrapper.jar"

IF DEFINED JAVA_HOME (
  SET "JAVA_EXEC=%JAVA_HOME%\bin\java.exe"
) ELSE (
  SET "JAVA_EXEC=java"
)

IF NOT EXIST "%WRAPPER_JAR%" (
  ECHO Maven wrapper JAR not found: %WRAPPER_JAR% 1>&2
  EXIT /B 1
)

"%JAVA_EXEC%" %MAVEN_OPTS% -classpath "%WRAPPER_JAR%" "-Dmaven.multiModuleProjectDirectory=%BASE_DIR%" org.apache.maven.wrapper.MavenWrapperMain %*
EXIT /B %ERRORLEVEL%
