To compile (EAP16):

1. Clone the source from Github (https://github.com/minman/wicketforge).
2. Create a *new* Plugin Project (*not* import) and set the working directory to the directory where you cloned the source.
3. Ensure you have the plugin-devkit selected as the project SDK.
4. Add the Properties plugin Jar to the plugin-SDK ( $IDEA_HOME/plugins/properties/lib/properties.jar).
5. Modify the version in resources/META-INF/plugin.xml
6. Build & Deploy (Build menu --> Prepare Plugin Module 'wicketforge' For Deployment)
7. Add the built JAR to your plugins (Install from disk...)