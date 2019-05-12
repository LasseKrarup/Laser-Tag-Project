const React = require("react");

const ReactDOM = require("react-dom");

class App extends React.Component {
  constructor(props) {
    super(props); // Players are sorted by id - their key is their ID

    this.state = {
      players: {
        byId: {},
        allIds: []
      }
    };
    this.addPlayer = this.addPlayer.bind(this);
    this.removePlayer = this.removePlayer.bind(this);
    this.startGame = this.startGame.bind(this);
  }

  addPlayer() {
    console.log("Add player");
    let activeIDs = this.state.players.allIds;
    let playerName = document.getElementById("playerNameInput").value;
    let kitNumber = document.getElementById("kitNumberSelectAdd").value;

    if (!activeIDs.includes(kitNumber)) {
      activeIDs.push(kitNumber);
      wsClient.send(JSON.stringify({
        action: "addPlayer",
        name: playerName,
        id: kitNumber
      })); // Update state. Must be done immutably, hence the spread operators

      this.setState({ ...this.state,
        players: { ...this.state.players,
          byId: { ...this.state.players.byId,
            [kitNumber]: {
              id: kitNumber,
              name: playerName,
              score: 0
            }
          },
          allIds: activeIDs
        }
      });
    } else {
      console.log("Player with ID " + kitNumber + " is already in the game");
    }
  }

  removePlayer(event) {
    event.preventDefault();
    let activeIDs = this.state.players.allIds;
    let kitNumber = document.getElementById("kitNumberSelectRemove").value;
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
      })); // Extract deleted player from state

      let {
        [kitNumber]: deleted,
        ...remainingPlayers
      } = this.state.players.byId;
      console.log(remainingPlayers); // Update state immutably

      this.setState({ ...this.state,
        players: {
          byId: remainingPlayers,
          allIds: activeIDs
        }
      });
    } else {
      console.log("Player with ID " + kitNumber + " is not in the game");
    }
  }

  startGame(event) {
    event.preventDefault();
    const gametime = document.getElementById("gametime").value;

    if (gametime >= 10 && gametime <= 20) {
      if (this.state.players.allIds.length > 1) {
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
      className: "col-2"
    }, React.createElement(FormArea, {
      players: this.state.players,
      addPlayer: this.addPlayer,
      removePlayer: this.removePlayer,
      startGame: this.startGame
    })), React.createElement("div", {
      className: "col-8 ml-auto"
    }, React.createElement(PlayerList, {
      players: this.state.players
    }))));
  }

}

class PlayerList extends React.Component {
  constructor(props) {
    super(props);
    this.sortHighscore = this.sortHighscore.bind(this);
  }

  sortHighscore(players) {
    let playerArray = [];

    for (let idx in players) {
      //convert to array for sorting
      playerArray.push([players[idx].id, players[idx].name, players[idx].score]);
    }

    let sorted = playerArray.sort((a, b) => {
      return b[0] - a[0]; // change to 2 to sort by score
    });
    return sorted;
  }

  render() {
    let playersSortedByHighscore = this.sortHighscore(this.props.players.byId);
    return React.createElement("div", {
      id: "playerList"
    }, React.createElement("h1", null, "Active players"), React.createElement("ol", null, playersSortedByHighscore.map((player, idx) => {
      /*  The players have been changed to an array for sorting.
          That means:
          player[0] = id
          player[1] = name
          player[2] = score
      */
      return React.createElement("li", {
        key: idx
      }, React.createElement("span", null, React.createElement("strong", null, player[1]), " ", React.createElement("small", null, "kit ", player[0])), React.createElement("span", {
        className: "points"
      }, player[2], " pts"));
    })));
  }

}

class FormArea extends React.Component {
  constructor(props) {
    super(props);
    this.handleAddPlayer = this.handleAddPlayer.bind(this);
    this.handleRemovePlayer = this.handleRemovePlayer.bind(this);
    this.handleStartGame = this.handleStartGame.bind(this);
  }

  handleAddPlayer(event) {
    event.preventDefault();
    console.log("Handle add player");
    this.props.addPlayer(event);
  }

  handleRemovePlayer(event) {
    this.props.removePlayer(event);
  }

  handleStartGame(event) {
    this.props.startGame(event);
  }

  render() {
    // Fill array of all non-active kit numbers
    let nonActiveIds = new Array();
    Array(10).fill(0).forEach((val, idx) => {
      if (!this.props.players.allIds.includes((idx + 1).toString())) {
        nonActiveIds.push(React.createElement("option", {
          key: idx,
          value: idx + 1
        }, idx + 1));
      }
    });
    let sortedIds = this.props.players.allIds.sort((a, b) => {
      return a - b;
    });
    return React.createElement("div", {
      id: "formArea"
    }, React.createElement("div", {
      className: "row"
    }, React.createElement("form", {
      onSubmit: this.handleAddPlayer,
      id: "addPlayerForm"
    }, React.createElement("h4", null, "Add player"), React.createElement("div", {
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
    }, nonActiveIds)), React.createElement("input", {
      type: "submit",
      className: "btn btn-primary",
      value: "Add player"
    }))), React.createElement("hr", null), React.createElement("div", {
      className: "row"
    }, React.createElement("form", {
      onSubmit: this.handleRemovePlayer,
      id: "removePlayerForm"
    }, React.createElement("h4", null, "Remove player"), React.createElement("div", {
      className: "form-group"
    }, React.createElement("label", null, "Kit ID:"), React.createElement("select", {
      className: "form-control",
      name: "kitnumber",
      id: "kitNumberSelectRemove"
    }, sortedIds.map((id, idx) => {
      return React.createElement("option", {
        key: idx,
        value: id
      }, id, " -", " ", this.props.players.byId[id].name);
    }))), React.createElement("input", {
      type: "submit",
      className: "btn btn-primary",
      value: "Remove player"
    }))), React.createElement("hr", null), React.createElement("div", {
      className: "row start-game-form"
    }, React.createElement("form", {
      className: "align-bottom"
    }, React.createElement("h4", null, "Start game"), React.createElement("div", {
      className: "form-group"
    }, React.createElement("label", null, "Gametime: "), React.createElement("input", {
      type: "number",
      min: "10",
      max: "20",
      id: "gametime",
      placeholder: "Enter the gametime..."
    })), React.createElement("button", {
      className: "btn btn-primary",
      onClick: this.handleStartGame
    }, "Start game"))));
  }

}

ReactDOM.render(React.createElement(App, null), document.getElementById("react-container"));