# IAP Tools for Java
A Java toolkit for working with the IAP network protocol and its data format ION. IAP is a network protocol intended to replace HTTP, and possibly other protocols too.

IAP encodes messages using a data format called ION.
ION can be used to encode a wide variety of data (e.g. the result from a remote procedure call),
and you can also nest other encodings (e.g raw bytes) inside ION.

ION can be used separately from IAP. It is very flexible, compact and fast.
You can use ION with IAP, with HTTP (instead of e.g. JSON), and also in log files
and data files.


## IAP Tools Tutorial
We have a tutorial for IAP Tools here:

http://tutorials.jenkov.com/iap-tools-java/index.html


## IAP / ION Specification
The official specifications for IAP and ION can be found here:

http://tutorials.jenkov.com/iap/index.html


## ION vs. Other Data Formats
If you are curious about ION's capabilities compared to other data formats, we have a rough comparision here:

http://tutorials.jenkov.com/iap/ion-vs-other-formats.html


## ION Benchmarks
As mentioned, ION is fast and compact. To back up this claim we have implemented a long list of benchmarks. You
can find these benchmarks here:

http://tutorials.jenkov.com/iap/ion-performance-benchmarks.html

The benchmark code is kept in a separate Github repository here:

https://github.com/jjenkov/iap-tools-java-benchmarks





