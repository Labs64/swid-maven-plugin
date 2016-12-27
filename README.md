# Maven Plugin :: SoftWare IDentification (SWID) Tags Generator

[![Stories in Ready](https://badge.waffle.io/Labs64/swid-maven-plugin.svg?label=ready&title=Ready)](http://waffle.io/Labs64/swid-maven-plugin)
[![Build Status](https://travis-ci.org/Labs64/swid-maven-plugin.svg)](https://travis-ci.org/Labs64/swid-maven-plugin)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.labs64.mojo/swid-maven-plugin/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.labs64.mojo/swid-maven-plugin)

Generate SoftWare IDentification (SWID) Tags based on the Maven POM according to [ISO/IEC 19770-2:2009](http://www.iso.org/iso/home/store/catalogue_tc/catalogue_detail.htm?csnumber=53670)

## What are SWID Tags?

SoftWare IDentification (SWID) Tags record unique information about an installed software application, including its name, edition, version, whether it’s part of a bundle and more. SWID tags support software inventory and asset management initiatives. The structure of SWID tags is specified in international standard [ISO/IEC 19770-2:2009](http://www.iso.org/iso/home/store/catalogue_tc/catalogue_detail.htm?csnumber=53670).

## Quick Start

The recommended way to get started using [`swid-maven-plugin`](https://maven-badges.herokuapp.com/maven-central/com.labs64.mojo/swid-maven-plugin) in your project is with a plugin management system – the Maven snippet below can be copied and pasted into your build.

Maven:
```xml
<plugins>
  <plugin>
    <groupId>com.labs64.mojo</groupId>
    <artifactId>swid-maven-plugin</artifactId>
    <version>0.1.0</version>
  </plugin>
</plugins>
```

## Examples

Please consult the [Examples](http://io.labs64.com/swid-maven-plugin/) section for information on how SWID Tags Generator plugin can be used.

## Compatibility

This library requires J2SE 1.6 or never. All dependencies are handled by Maven.

## Links
- Plugin website: http://io.labs64.com/swid-maven-plugin/
- SoftWare IDentification (SWID) Tags Generator (Java Library): https://github.com/Labs64/swid-generator/
