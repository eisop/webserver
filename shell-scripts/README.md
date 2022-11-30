### Detailed instructions on configuring the checkerframework with deploy-checkerweb.sh

if using deploy-checkerweb.sh with no flags runs into issues with finding the processors, ensure the following is completed

1. Install maven
```
    apt install maven
```
2. In the local clone of the checkerframework, run;
```
    ./gradlew
```
3. If issues persist, run deply-checkerweb.sh with this flag;
```
    ./shell-scripts/deploy-checkerweb.sh -l <path to checker-framework>
```
e.g.
```
    ./shell-scripts/deploy-checkerweb.sh -l ../jsr308/checker-framework
```
4. As a last resort, manually set the correct path to the CF variable in the run-checkerweb.sh file 
```
    CF=/home/[your_user]/var/www/webserver/[local_copy_folder]/checker-framework
```
