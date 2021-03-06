const React = require("react");

const ReactDOM = require("react-dom");

class App extends React.Component {
  constructor(props) {
    super(props); // Players are sorted by id - their key is their ID

    this.state = {
      players: {
        byId: {},
        allIds: []
      },
      time: {
        minutes: 0,
        seconds: 0
      },
      modal: {
        visible: false,
        message: ""
      },
      countdown: 5,
      showCountdown: false
    };
    this.interval;
    this.countdownInterval;
    this.addPlayer = this.addPlayer.bind(this);
    this.removePlayer = this.removePlayer.bind(this);
    this.startGame = this.startGame.bind(this);
    this.handleUpdateHighscore = this.handleUpdateHighscore.bind(this);
    this.handleStartPractice = this.handleStartPractice.bind(this);
    this.handleStartTimer = this.handleStartTimer.bind(this);
    this.toggleModal = this.toggleModal.bind(this);
  } // Listen for incoming messages in lifetime method


  componentDidMount() {
    wsClient.addEventListener("message", msg => {
      msg = JSON.parse(msg.data);

      switch (msg.action) {
        case "highscoreUpdate":
          console.log("Highscore update received from server");
          this.handleUpdateHighscore(msg);
          break;

        case "startPractice":
          {
            console.log("Start practice received  from server");
            this.handleStartPractice();
            break;
          }

        default:
          console.log("Unknown message received from server");
          break;
      }
    }); // Stop timer

    ipcRenderer.on("stopGame", () => {
      clearInterval(this.interval);
      this.toggleModal("The game has finished! Check out your high score. Did you win?");
      this.setState({ ...this.state,
        time: {
          minutes: 0,
          seconds: 0
        }
      });
    });
  }

  handleUpdateHighscore(msg) {
    this.setState({ ...this.state,
      players: { ...this.state.players,
        byId: { ...this.state.players.byId,
          [msg.id]: { ...this.state.players.byId[msg.id],
            score: parseInt(msg.score)
          }
        }
      }
    });
  }

  handleStartPractice() {
    if (this.state.players.allIds.length != 0) {
      if (this.state.players.minutes == 0 && this.state.players.seconds == 0) {
        this.setState({ ...this.state,
          time: {
            minutes: 1,
            seconds: 0
          },
          countdown: 5,
          showCountdown: true
        }, () => {
          this.countdownInterval = setInterval(() => {
            this.setState({ ...this.state,
              countdown: this.state.countdown - 1
            });

            if (this.state.countdown == 0) {
              clearInterval(this.countdownInterval);
              this.setState({ ...this.state,
                showCountdown: false
              }); // Add fake player (practice kit)

              wsClient.send(JSON.stringify({
                action: "addPlayer",
                name: "Practice Kit",
                id: "11"
              })); // Send start game

              wsClient.send(JSON.stringify({
                action: "startGame",
                time: "1"
              }));
              this.handleStartTimer();
            }
          }, 1000);
        });
      }
    } else {
      console.log("No players added - can't start practice");
      this.toggleModal("Oh no! You haven't added any players, so your precious high score won't be saved anywhere. Turn off the practice kit, add a player and turn the practice kit back on!");
    }
  }

  toggleModal(message = "") {
    if (this.state.modal.visible) {
      this.setState({ ...this.state,
        modal: { ...this.state.modal,
          visible: false
        }
      });
    } else {
      this.setState({ ...this.state,
        modal: {
          message: message,
          visible: true
        }
      });
    }
  }

  addPlayer() {
    let activeIDs = this.state.players.allIds;
    let playerName = document.getElementById("playerNameInput").value;
    let kitNumber = document.getElementById("kitNumberSelectAdd").value;

    if (activeIDs.length >= 10) {
      console.log("Player list full");
      this.toggleModal("You can't play Laser Tag with more than 10 people. Do you even have more than 10 friends? No? Didn't think so.");
    } else if (!activeIDs.includes(kitNumber)) {
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
      this.toggleModal("Player with ID " + kitNumber + " is already in the game");
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
      } = this.state.players.byId; // Update state immutably

      this.setState({ ...this.state,
        players: {
          byId: remainingPlayers,
          allIds: activeIDs
        }
      });
    } else {
      console.log("Player with ID " + kitNumber + " is not in the game");
      this.toggleModal("Player is not in the game. You can't remove someone who doesn't exist, dum-dum.");
    }
  }

  startGame(event) {
    event.preventDefault();
    const gametime = document.getElementById("gametime").value;

    if (this.state.time.minutes == 0 && this.state.time.seconds == 0) {
      if (gametime >= 10 && gametime <= 20) {
        if (this.state.players.allIds.length > 1) {
          wsClient.send(JSON.stringify({
            action: "startGame",
            time: gametime
          })); // Reset the score of all players

          let newPlayers = {};
          this.state.players.allIds.map(id => {
            newPlayers = { ...newPlayers,
              [id]: { ...this.state.players.byId[id],
                score: 0
              }
            };
          }); // Set state with time and new scores (the reset ones)

          this.setState({ ...this.state,
            players: { ...this.state.players,
              byId: newPlayers
            },
            time: { ...this.state.time,
              minutes: gametime
            }
          }, this.handleStartTimer());
        } else {
          console.log("Not enough players");
          this.toggleModal("Not enough players. Don't you have any friends? You need at least 2 people to play");
        }
      } else {
        console.log("Game time not withing range");
        this.toggleModal("The game must be between 10 and 20 minutes. Don't you have the stamina for that? Too bad!");
      }
    } else {
      console.log("A game is already running!");
      this.toggleModal("A game is already running!");
    }
  }

  handleStartTimer() {
    this.interval = setInterval(() => {
      if (this.state.time.seconds == 0) {
        // Tick minutes
        this.setState({ ...this.state,
          time: {
            minutes: this.state.time.minutes - 1,
            seconds: 59
          }
        });
      } else {
        // Tick seconds
        this.setState({ ...this.state,
          time: { ...this.state.time,
            seconds: this.state.time.seconds - 1
          }
        });
      }

      if (this.state.time.minutes == 0 && this.state.time.seconds == 0) {
        clearInterval(this.interval);
        ipcRenderer.send("stopGame");
      }
    }, 1000);
  }

  render() {
    return React.createElement("div", {
      className: "container"
    }, React.createElement(Countdown, {
      isVisible: this.state.showCountdown,
      message: this.state.countdown
    }), React.createElement("div", {
      className: "row"
    }, React.createElement("div", {
      className: "col-2"
    }, React.createElement(FormArea, {
      players: this.state.players,
      addPlayer: this.addPlayer,
      removePlayer: this.removePlayer,
      startGame: this.startGame,
      handleModal: this.toggleModal,
      modal: this.state.modal
    })), React.createElement("div", {
      className: "col-8 ml-auto"
    }, React.createElement(PlayerList, {
      players: this.state.players
    }))), React.createElement(GameTimer, {
      minutes: this.state.time.minutes,
      seconds: this.state.time.seconds
    }));
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
      return b[2] - a[2]; // change to 2 to sort by score
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
    this.handleModal = this.handleModal.bind(this);
  }

  handleAddPlayer(event) {
    event.preventDefault();
    this.props.addPlayer(event);
  }

  handleRemovePlayer(event) {
    this.props.removePlayer(event);
  }

  handleStartGame(event) {
    this.props.startGame(event);
  }

  handleModal() {
    this.props.handleModal();
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
    }, React.createElement(Modal, {
      isVisible: this.props.modal.visible,
      message: this.props.modal.message,
      toggleModal: this.props.handleModal
    }), React.createElement("div", {
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
      }, this.props.players.byId[id].name, " -", " ", "kit ", id);
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
    }, React.createElement("label", null, "Game time: "), React.createElement("input", {
      type: "number",
      min: "10",
      max: "20",
      id: "gametime",
      placeholder: "Game time..."
    })), React.createElement("button", {
      className: "btn btn-primary",
      onClick: this.handleStartGame
    }, "Start game"))));
  }

}

class GameTimer extends React.Component {
  render() {
    let min = this.props.minutes;
    let sec = this.props.seconds;
    return React.createElement("div", {
      className: "gameTimer" + (min < 2 ? " lowTime " : "") + (min == 0 && sec == 0 ? " hidden" : " visible")
    }, min < 10 ? "0" + min : min, ":", sec < 10 ? "0" + sec : sec);
  }

}

class Modal extends React.Component {
  render() {
    return React.createElement("div", {
      className: "modalContainer"
    }, React.createElement("div", {
      className: "overlay " + (this.props.isVisible ? "visible" : "hidden")
    }), React.createElement("div", {
      className: "customModal " + (this.props.isVisible ? "visible" : "hidden")
    }, this.props.message, React.createElement("button", {
      className: "btn btn-danger btn-topright",
      onClick: this.props.toggleModal
    }, "\xD7")));
  }

}

class Countdown extends React.Component {
  render() {
    return React.createElement("div", {
      className: "modalContainer"
    }, React.createElement("div", {
      className: "overlay " + (this.props.isVisible ? "visible" : "hidden")
    }), React.createElement("div", {
      className: "customCountdown " + (this.props.isVisible ? "visible" : "hidden")
    }, React.createElement("div", {
      className: "countdown-content"
    }, React.createElement("span", null, "Practice starts in..."), React.createElement("br", null), React.createElement("span", {
      className: "countdown-text"
    }, this.props.message))));
  }

}

ReactDOM.render(React.createElement(App, null), document.getElementById("react-container"));