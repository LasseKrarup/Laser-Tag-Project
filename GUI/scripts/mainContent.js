const React = require("react");

const ReactDOM = require("react-dom");

class App extends React.Component {
  render() {
    return React.createElement("div", null, React.createElement(FormArea, null), React.createElement(PlayerList, null));
  }

}

class PlayerList extends React.Component {
  render() {
    return React.createElement("h1", null, "Active players");
  }

}

class FormArea extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      activeIDs: []
    };
    this.addPlayer = this.addPlayer.bind(this);
    this.removePlayer = this.removePlayer.bind(this);
    this.startGame = this.startGame.bind(this);
  }

  addPlayer(event) {
    event.preventDefault();
    let activeIDs = this.state.activeIDs;
    const playerName = document.getElementById("playerNameInput").value;
    const kitNumber = document.getElementById("kitNumberSelectAdd").value;

    if (!activeIDs.includes(kitNumber)) {
      activeIDs.push(kitNumber);
      wsClient.send(JSON.stringify({
        action: "addPlayer",
        name: playerName,
        id: kitNumber
      }));
      this.setState({
        activeIDs: activeIDs
      });
    } else {
      console.log("Player with ID " + kitNumber + " is already in the game");
    }
  }

  removePlayer(event) {
    event.preventDefault();
    let activeIDs = this.state.activeIDs;
    const kitNumber = document.getElementById("kitNumberSelectRemove").value;
    /*
        If the ID is in the list of active ID's,
        then remove the ID and request server
        to remove it as well
    */

    let index = activeIDs.indexOf(kitNumber);

    if (index > -1) {
      activeIDs.splice(index, 1);
      wsClient.send(JSON.stringify({
        action: "removePlayer",
        id: kitNumber
      }));
      this.setState({
        activeIDs: activeIDs
      });
    } else {
      console.log("Player with ID " + kitNumber + " is not in the game");
    }
  }

  startGame(event) {
    event.preventDefault();
    const gametime = document.getElementById("gametime").value;

    if (gametime >= 10 && gametime <= 20) {
      if (activeIDs.length > 1) {
        wsClient.send(JSON.stringify({
          action: "startGame",
          time: gametime
        }));
      } else {
        console.log("Not enough players");
      }
    } else {
      console.log("Gametime not withing range");
    }
  }

  render() {
    return React.createElement("div", {
      className: "container"
    }, React.createElement("div", {
      className: "row"
    }, React.createElement("div", {
      className: "col"
    }, React.createElement("h4", null, "Add player"), React.createElement("form", {
      onSubmit: this.addPlayer,
      id: "addPlayerForm"
    }, React.createElement("div", {
      className: "form-group"
    }, React.createElement("label", null, "Player: "), React.createElement("input", {
      className: "form-control",
      type: "text",
      name: "name",
      id: "playerNameInput",
      placeholder: "Name of player"
    })), React.createElement("div", {
      className: "form-group"
    }, React.createElement("label", null, "Kit ID:"), React.createElement("select", {
      className: "form-control",
      name: "kitnumber",
      id: "kitNumberSelectAdd"
    }, React.createElement("option", {
      value: "1"
    }, "1"), React.createElement("option", {
      value: "2"
    }, "2"), React.createElement("option", {
      value: "3"
    }, "3"), React.createElement("option", {
      value: "4"
    }, "4"), React.createElement("option", {
      value: "5"
    }, "5"), React.createElement("option", {
      value: "6"
    }, "6"), React.createElement("option", {
      value: "7"
    }, "7"), React.createElement("option", {
      value: "8"
    }, "8"), React.createElement("option", {
      value: "9"
    }, "9"), React.createElement("option", {
      value: "10"
    }, "10"))), React.createElement("input", {
      type: "submit",
      className: "btn btn-primary",
      value: "Add player"
    }))), React.createElement("div", {
      className: "col"
    }, React.createElement("h4", null, "Remove player"), React.createElement("form", {
      onSubmit: this.removePlayer,
      id: "removePlayerForm"
    }, React.createElement("div", {
      className: "form-group"
    }, React.createElement("label", null, "Kit ID:"), React.createElement("select", {
      className: "form-control",
      name: "kitnumber",
      id: "kitNumberSelectRemove"
    }, React.createElement("option", {
      value: "1"
    }, "1"), React.createElement("option", {
      value: "2"
    }, "2"), React.createElement("option", {
      value: "3"
    }, "3"), React.createElement("option", {
      value: "4"
    }, "4"), React.createElement("option", {
      value: "5"
    }, "5"), React.createElement("option", {
      value: "6"
    }, "6"), React.createElement("option", {
      value: "7"
    }, "7"), React.createElement("option", {
      value: "8"
    }, "8"), React.createElement("option", {
      value: "9"
    }, "9"), React.createElement("option", {
      value: "10"
    }, "10"))), React.createElement("input", {
      type: "submit",
      className: "btn btn-primary",
      value: "Remove player"
    }))), React.createElement("div", {
      className: "col start-game-form"
    }, React.createElement("h4", null, "Start game"), React.createElement("form", {
      className: "align-bottom"
    }, React.createElement("div", {
      className: "form-group"
    }, React.createElement("label", null, "Gametime: "), React.createElement("input", {
      type: "number",
      min: "10",
      max: "20",
      id: "gametime",
      placeholder: "Enter the gametime..."
    })), React.createElement("button", {
      className: "btn btn-primary",
      onClick: this.startGame
    }, "Start game")))));
  }

}

ReactDOM.render(React.createElement(App, null), document.getElementById("react-container"));