Java GraphQL Schema Generation and Execution Framework
========

# Table of Contents
- [Overview](#overview)
### Overview

This is a java to GraphQL schema generation and execution package. This originated the week of January 18th, 2016 as a hackweek 
project I worked on.  The goal of this project is a production level quality project that can be used to build
a Java based GraphQL server. A following principles will guide it's evolution:
- An unopinionated view of the container the server will be running with.
- An unopinionated view of the serialization model you will be using for results.
- A minimal set of dependencies to utlize the framework.

Initial Verions will be support Java 7 and require the Guava Module. Future versions will be based on Java 8 and
possibly require the Guava module (TBD).

Dependencies
 - Guava (regardless if you include the guava type mapping package)
 - SLF4J (logging)
 - GraphQL-Java
