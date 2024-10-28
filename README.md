Put this app in a separate directory than your t27 project folder. The build script recursively searches for a pom.xml file
in the parent directory of the /bin folder. If this project is at the same height as your project, it might find the other pom first. 
