# StreamLine

[![Java CI](https://github.com/wellatleastitried/StreamLine/actions/workflows/maven.yml/badge.svg)](https://github.com/wellatleastitried/StreamLine/actions/workflows/maven.yml)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/wellatleastitried/StreamLine/blob/main/LICENSE)
[![Java Version](https://img.shields.io/badge/java-17%2B-blue)](https://www.oracle.com/java/technologies/downloads/)
[![Maven](https://img.shields.io/badge/maven-3.8%2B-orange)](https://maven.apache.org/)

StreamLine is a modern, terminal-based music player built in Java. It provides a lightweight, privacy-focused alternative to traditional music streaming platforms, leveraging the [Invidious API](https://docs.invidious.io/) for content delivery.

## Features

- Terminal-based user interface using [Lanterna](https://github.com/mabe02/lanterna)
- Advanced search capabilities
- Download and cache management
- Customizable themes and layouts
- Playlist management
- Now playing information
- Privacy-focused design
- Fast and efficient performance

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.8 or higher
- yt-dlp (for audio extraction)

### Installation

1. Clone the repository:
```bash
git clone https://github.com/wellatleastitried/StreamLine.git
cd StreamLine
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
java -jar target/StreamLine.jar
```

## Documentation

- [User Guide](docs/USER_GUIDE.md) - Learn how to use StreamLine
- [Developer Guide](docs/DEVELOPER_GUIDE.md) - Contribute to the project
- [API Documentation](docs/API.md) - Technical documentation

## Contributing

We welcome contributions! Please read our [Contributing Guidelines](CONTRIBUTING.md) for details on our code of conduct and the process for submitting pull requests.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [Lanterna](https://github.com/mabe02/lanterna) - Terminal UI library
- [Invidious](https://github.com/iv-org/invidious) - Privacy-focused YouTube frontend
- [yt-dlp](https://github.com/yt-dlp/yt-dlp) - YouTube-DL fork with additional features

## Support

For support, please:
- Check the [documentation](doc/)
- Open an [issue](https://github.com/wellatleastitried/StreamLine/issues)
- Join our [Discord community](https://discord.gg/streamline)

## Project Status

This project is currently under active development. While core functionality is implemented, we are continuously working on:
- [ ] Enhanced playlist management
- [ ] Improved caching system
- [ ] Additional theme options
- [ ] Performance optimizations
- [ ] Extended documentation

Check our [project board](https://github.com/wellatleastitried/StreamLine/projects) for the latest updates and planned features.
