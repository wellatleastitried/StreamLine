# Theme Customization

Change the look and feel of StreamLine by modifying the SteamLine configuration file.

## Instructions

Navigate to the configuration file and open it with your preferred text editor:
```bash
cd ~/.local/share/StreamLine/config
<preferred_text_editor> config.properties
```
Change the theme by modifying the `theme` property in the configuration file. The list of available themes is the following:
- `default`
- `dark`
- `light`
- `solarized`

```yaml
theme=<preferred_theme>
```

Save the changes and restart StreamLine to apply the new theme.
