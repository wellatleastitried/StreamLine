# StreamLine 🎵
StreamLine is a terminal-based (TUI) music player built in Java using the [Lanterna](https://github.com/mabe02/lanterna) library. It integrates with the [Invidious API](https://docs.invidious.io/) to provide a free, lightweight alternative to traditional music streaming platforms.

## NOTICE
This program is currently under development and is not yet functional. If you want to help this project's timeline move forward, please feel free to [contribute](https://github.com/wellatleastitried/StreamLine/blob/main/CONTRIBUTING.md)!

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

3. Install the project (*Optional*, but recommended)
```bash
java -jar target/streamline.jar --install
```

4. Run the application with the `--setup` flag to install the final requirements for the app:
</br></br>Using `--setup youtube` is recommended - the invidious instances are inconsistent and may lead to issues.
```bash
streamline --setup <youtube|docker>
```

5. Run the application:
```bash
streamline
```

## Documentation

- [User Guide](docs/USER_GUIDE.md) - Learn how to use StreamLine
- [Developer Guide](CONTRIBUTING.md) - Contribute to the project
- [API Documentation](docs/API.md) - Technical documentation
- [Changing Themes](docs/THEMES.md) - Customize the look and feel of StreamLine

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

## Project Status

This project is currently under active development. While core functionality is implemented, we are continuously working on:
- [ ] Enhanced playlist management
- [ ] Improved caching system
- [ ] Additional theme options
- [ ] Performance optimizations
- [ ] Extended documentation

Check our [project board](https://github.com/wellatleastitried/StreamLine/projects) for the latest updates and planned features.
