## Box Of Bagel Bot

This is a Twitch chat bot using an implementation of [PircBot](http://www.jibble.org/pircbot.php) for basic ask-and-receive commands.

### Building:
This project uses Maven to build and is set to build with Java 16. I don't believe it uses any Java 16-specific functionality, so it's very likely you can run with any older or newer Java version.
```
cd /path/to/where/you/want/to/clone
git clone https://github.com/JohnStar128/boxofbagelbot.git
mvn package
java -jar target/BoxOfBagelBot-2.0-SNAPSHOT.jar
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

### Placeholders
Placeholders are special strings you can put into your commands to replace with variable data when a user runs the command.\
Currently there are two placeholders:
- coin -- Returns `heads` or `tails` with a 50% chance.
- time -- Returns the current system time in `hh:mm MM/dd/yyyy` format.

An example of placeholders being used:
```yml 
commands:
  - name: "flipacoin"
    args: "The coin landed on ${coin}."
  - name: "whattimeisit"
    args: "It is currently ${time}."
```
```
<username>: !flipacoin
<boxofbagelbot>: The coin landed on tails.
<username>: !whattimeisit
<boxofbagelbot>: It is currently 11:57 on 01/28/21.
```

