11/29/12 12:14:59 Thu
Simple in memory filesystem By Eric Harrison

Here is an example of using the commandline:

run com.crawlicious.filesystem.cli.Main by doing the following

mvn test
mvn assembly:assembly
java -jar target/filesystem-0.0.1-SNAPSHOT-jar-with-dependencies.jar


> mkdrive c:
created drive: c:
> ls c:
[]
> mkdir c:/foo
created folder, parent=c: folder=foo
> write c:/foo/bar.txt
[mkdrive drive | mkdir folder | write file text... | mv source dest | cat file | ls folder | rm file | quit]
not enough args
> write c:/foo/bar.txt hi bar
created file c:/foo/bar.txt text=hi bar 
> cat c:/foo/bar.txt
hi bar 
> quit



