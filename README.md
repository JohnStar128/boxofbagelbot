## Box Of Bagel Bot

This is a Twitch chat bot using an implementation of [PircBot](http://www.jibble.org/pircbot.php) for basic ask-and-receive commands.

This bot allows you to create and remove custom commands.

### Hardcoded commands:

- !about -- Print information about the bot.
- !addcommand \<name> \<output> -- Return a message when a user types <name>
- !removecommand \<name> -- Remove the return message associated with <name>
- !commands -- List all hardcoded and user-added commands,
- !coin return the result of a 50/50 chance.


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