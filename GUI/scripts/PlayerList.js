const React = require("react");

class PlayerList extends React.Component {
  render() {
    return React.createElement("h1", null, "Active players");
  }

}

module.exports = {
  PlayerList: PlayerList
};