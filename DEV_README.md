To compile (IDEA 2016.3.3):

1. Clone the source from Github (https://github.com/minman/wicketforge).
2. Create a *new* Plugin Project (*not* import) and set the working directory to the directory where you cloned the source.
3. Ensure you have the plugin-devkit selected as the project SDK.
4. Add the Properties plugin Jar to the plugin-SDK ( $IDEA_HOME/plugins/properties/lib/properties.jar).
5. Ensure that 'Project Settings -> Modules -> Plugin Deployment' points to the correct /META-INF/plugin.xml not to the /resources/META-INF/plugin.xml created by IDEA by default
6. Build & Deploy (Build menu --> Prepare Plugin Module 'wicketforge' For Deployment)