# StreamLine 🎵
StreamLine is a terminal-based (TUI) music player built in Java using the [Lanterna](https://github.com/mabe02/lanterna) library. It integrates with the [Invidious API](https://docs.invidious.io/) to provide a free, lightweight alternative to traditional music streaming platforms.

## Table of Contents
- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [Building from Source](#building-from-source)
- [Requirements](#requirements)
- [Contributing](#contributing)
- [License](#license)

## Features
- **Terminal-Based Interface**: Intuitive text-based UI for a lightweight experience.
- **Search for Songs**: Allows users to search for songs directly from Invidious.
- **Liked songs and Playlists**: View your liked songs and create/manage your playlists.
- **Recently Played**: Track your recently played songs.
- **Help menu**: Provides guidance on how to use StreamLine.

## Installation
This method is not yet available, StreamLine 0.0.1 will be releasing soon!
### For Debian-Based Systems (Ubuntu, etc.)
You can download the '.deb' package from the releases page and install it using 'dpkg':
```bash
sudo dpkg -i streamline.deb
```
### For Arch-Based Systems
You can install StreamLine using the .pkg.tar.zst:
```bash
sudo pacman -U streamline.pkg.tar.zst
```
### For Other Linux Distributions
You can manually install the .tar.gz file and run the compiled JAR. An example of this would be:
```bash
sudo cp streamline /usr/local/bin/streamline
```
Now you can run streamline from the terminal:
```bash
streamline
```
## Usage
Once installed, run the program using
```bash
streamline
```
### Main Menu Options
- **Search for a song**: Start searching for your favorite songs by interacting with the Invidious API.
- **View Liked Music**: Browse your liked songs.
- **Playlists**: View and manage your playlists.
- **Recently Played**: View your recently played songs.
- **Help**: Access help information for using StreamLine.
- **Quit**: Exit the application.
## Building From Source
### Requirements to Build:
- Git
- Java 17+
- Maven
### Steps to Build:
1. Clone the repository:
```bash
git clone https://github.com/wellatleastitried/StreamLine.git
cd StreamLine
```
2. Build the project using Maven:
```bash
mvn install
```
3. ***Optional:*** Run the application setup for hosting a local Docker instance:
```bash
java -jar target/streamline-VERSION.jar --setup
```
While this step ***is*** optional, using this app without Docker [may be inconsistent](#Notice)
4. Run the application:
```bash
java -jar target/streamline-VERSION.jar
```
## Requirements
- Java 17+
- Terminal with support for text-based interfaces
- Internet connection (If you intend to search for/listen to songs that have not been downloaded)
- If you want to have a locally hosted Invidious instance (can be quicker, failsafe is APIs are down):
  - Latest version of Docker
## Notice
The public instances of Invidious can change or go down at a moments notice. I will be trying to keep the list of API instances used by StreamLine updated, but running a local Docker instance will ensure that the app will always have online functionality. If you choose not to use Docker, StreamLine will only have online functionality if the public Invidious instances are reachable.
## Contributing
Contributions are welcome! Please check out [CONTRIBUTING.md](https://github.com/wellatleastitried/StreamLine/CONTRIBUTING.md) before getting started. Make sure to follow the project's code style and ensure all tests pass before submitting.

## License
This project is licensed under the MIT license. See the [License](https://github.com/wellatleastitried/StreamLine/LICENSE) file for details.
