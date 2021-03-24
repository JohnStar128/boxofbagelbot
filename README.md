## Box Of Bagel Bot

This is a Twitch chat bot using an implementation of [PircBot](http://www.jibble.org/pircbot.php) for basic ask-and-receive commands.

### Building:

```
cd /path/to/where/you/want/to/clone
git clone https://github.com/JohnStar128/boxofbagelbot.git
mvn package
java -jar target/TwitchBot1.jar
Edit config.json with appropriate values
``` 
Since the config and command storages will be generated in the working directory the first time the bot is run, it's recommended to move the jar file into its own directory.

### Hardcoded commands:

- !about -- Print information about the bot.
- !addcommand \<name> \<output> -- Return a message when a user types <name>
- !removecommand \<name> -- Remove the return message associated with <name>
- !commands -- List all hardcoded and user-added commands
- !editcommand \<name> -- Edit an existing command.


### Placeholders:
Placeholders are strings surrounded by percentage signs `%`. Adding placeholders in your command will run return dynamic output.\
Available placeholders are:
```
%coin% -- Return heads or tails
```


### Configuration

The bot uses a JSON configuration format to handle its configuration options and command storage.

`config.json` contains two fields:

- channel -- The Twitch chat the bot should join. Must be prefixed with a hash.
- oauth -- The oAuth token of the account the bot should use. Get your or your bot's oAuth token from https://twitchapps.com/tmi/

`config.json`:
```json
{
  "channel": "#channel-goes-here",
  "oauth": "token-goes-here"
}
```
`commands.json`:
```json
{
  "!command": "Returns this message a user starts their message with \"!command\".",
  "test": "Returns this message when a user starts their message with \"test\"."
}
```