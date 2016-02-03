# IAP Tools for Java (iap-tools-java)
A Java toolkit for working with the IAP network protocol and its data format ION.

IAP is a network protocol intended to replace HTTP, and possibly other protocols too.

IAP has a standard, binary data format called ION. ION is used to encode IAP messages.
ION can also be used to encode data (e.g. the result from a remote procedure call),
but you can also nest other encodings as raw bytes inside ION.

ION can be used separately from IAP. It is very flexible, compact and fast.
You can use ION with IAP, with HTTP (instead of e.g. JSON), and also in log files
and data files.


## IAP Tools Tutorial
We have a tutorial for IAP Tools here:

http://tutorials.jenkov.com/iap-tools-java/index.html


## IAP / ION Specification
The official specifications for IAP and ION can be found here:

http://tutorials.jenkov.com/iap/index.html


## ION Benchmarks
As mentioned, ION is fast and compact. To back up this claim we have implemented a long list of benchmarks. You
can find these benchmarks here:

http://tutorials.jenkov.com/iap/ion-performance-benchmarks.html

The benchmark code is kept in a separate Github repository here:

https://github.com/jjenkov/iap-tools-java-benchmarks





