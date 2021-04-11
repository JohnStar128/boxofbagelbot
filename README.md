## Box Of Bagel Bot

This is a Twitch chat bot using an implementation of [PircBot](http://www.jibble.org/pircbot.php) for basic ask-and-receive commands.

### Building:
This project uses Maven to build and is set to build with Java 14. I don't believe it uses any Java 14-specific functionality, so it's very likely you can run with any older or newer Java version.
```
cd /path/to/where/you/want/to/clone
git clone https://github.com/JohnStar128/boxofbagelbot.git
mvn package
java -jar target/BoxOfBagelBot-2.0.jar
Stop the bot and edit config.yml with appropriate values
``` 
Since the config and command storages will be generated in the working directory the first time the bot is run, it's recommended to move the jar file into its own directory.

### Hardcoded commands:

- !about -- Print information about the bot.
- !addcommand \<name> \<output> -- Set a message to be sent when a user types \<name>
- !removecommand \<name> -- Remove the return message associated with \<name>
- !commands -- List all hardcoded and user-added commands
- !editcommand \<name> -- Edit an existing command.

### Configuration

The bot uses a YAML configuration format to handle its configuration options and command storage.

`config.yml` contains three fields:

- channel -- The Twitch chat the bot should join. Must be prefixed with a hash.
- oauth -- The oAuth token of the account the bot should use. Get your or your bot's oAuth token from https://twitchapps.com/tmi/
- prefix -- The prefix character used to reference custom commands.

`config.yml`:
```yml
channel: "#channel-goes-here"
oauth: "oauth-goes-here"
prefix: "!"  
```
`commands.yml`:
```yml
commands:
  - name: "test"
    args: "This is what !test returns"
  - name: "hi"
    args: "This is what !hi returns"

```