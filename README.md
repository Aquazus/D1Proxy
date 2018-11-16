<a href="https://github.com/Aquazus/D1Proxy"><img src="https://vgy.me/GGGxB2.png" title="D1Proxy" alt="D1Proxy"></a>

# D1Proxy

> A simple yet powerful Java 11 TCP MITM proxy for Dofus 1.29.1

[![Maintainability](https://api.codeclimate.com/v1/badges/f087b0e25f3628654f55/maintainability)](https://codeclimate.com/github/Aquazus/D1Proxy/maintainability) [![Dependencies](https://img.shields.io/librariesio/github/Aquazus/D1Proxy.svg?style=flat-square)](http://libraries.io/github/Aquazus/D1Proxy) [![Github Issues](https://img.shields.io/github/issues-raw/Aquazus/D1Proxy.svg?style=flat-square)](https://github.com/Aquazus/D1Proxy/issues) [![Pending Pull-Requests](https://img.shields.io/github/issues-pr-raw/Aquazus/D1Proxy.svg?style=flat-square)](https://github.com/Aquazus/D1Proxy/pulls) [![License](https://img.shields.io/github/license/Aquazus/D1Proxy.svg?style=flat-square)](LICENSE)

[![D1Proxy](http://i.imgur.com/Msen5Cs.png)](#)

---

## Table of Contents

- [Installation](#installation)
- [Features](#features)
- [Contributing](#contributing)
- [Team](#team)
- [FAQ](#faq)
- [Support](#support)
- [License](#license)

---

## Installation

- [Install a MongoDB server](https://docs.mongodb.com/manual/installation/) on your system
- Make sure you have an IDE that includes the Java 11 Developer Kit and Maven

### Clone

- Clone this repo to your local machine using `https://github.com/Aquazus/D1Proxy.git`

### Compile

- Import the project using the pom.xml into your favorite IDE and run a Maven `package` goal
- You will find the jar file inside the target folder, named `d1proxy-<version>-jar-with-dependencies.jar`

### Setup

- Make sure the `d1proxy.properties` configuration file is in the same folder as your jar file
- Configure the proxy as needed
- Run the Proxy with Java 11 (or use one of the provided start script)

---

## Features

- **6 commands**
- (currently) **Undetected** by Ankama (but use at your own risk)
- **Community-based** data sniffing
- **Quality of Life** features to improve players experience

---

## Contributing

> To get started...

### Step 1

- **Option 1**
    - 🍴 Fork this repo!

- **Option 2**
    - 👯 Clone this repo to your local machine using `https://github.com/Aquazus/D1Proxy.git`

### Step 2

- **HACK AWAY!** 🔨🔨🔨

### Step 3

- 🔃 Create a new pull request using <a href="https://github.com/Aquazus/D1Proxy/compare/" target="_blank">`https://github.com/Aquazus/D1Proxy/compare/`</a>.

---

## Team

| <a href="https://github.com/Aquazus" target="_blank">**Aquazus**</a> |
| :---: |
| [![Aquazus](https://avatars1.githubusercontent.com/u/7611808?v=3&s=200)](https://github.com/Aquazus) |
| <a href="https://github.com/Aquazus" target="_blank">`github.com/Aquazus`</a> |
---

## FAQ

- **How to disable the community sniffing features?**
    - Set `proxy.sniffing` to `false`
- **How to run the proxy without MongoDB?**
    - Sadly, you can't.
- **How can people connect to my proxy?**
    - 1) Make sure `proxy.ip` is set on your WAN IP address
    - 2) Make sure the `proxy.port` provided is not blocked by your router and/or firewall
    - 3) Distribute them a config.xml that includes a corresponding connserver value.

---

## Support

Reach out to me at one of the following places!

- Twitter at <a href="http://twitter.com/Aquazus" target="_blank">`@Aquazus`</a>
- Discord at <a href="https://discord.gg/xUEpc5N" target="_blank">`discord.gg/xUEpc5N`</a>

---

## Donations

[![Liberapay receiving](http://img.shields.io/liberapay/receives/Aquazus.svg?logo=liberapay)](https://liberapay.com/Aquazus/) [![Liberapay patrons](http://img.shields.io/liberapay/patrons/Aquazus.svg?logo=liberapay)](https://liberapay.com/Aquazus/)

[![Donate using Liberapay](https://liberapay.com/assets/widgets/donate.svg)](https://liberapay.com/Aquazus/)

Bitcoin: `1JeSvkGhB6k81kDkUvxTizQTrf88P6fHaR`


---

## License

[![License](https://img.shields.io/github/license/Aquazus/D1Proxy.svg?style=flat-square)](LICENSE)

- **[AGPL-3.0 license](https://opensource.org/licenses/AGPL-3.0)**
- Copyright 2018 © <a href="http://github.com/Aquazus" target="_blank">Aquazus</a>.