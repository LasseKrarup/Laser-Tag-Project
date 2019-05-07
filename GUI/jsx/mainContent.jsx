const React = require("react");
const ReactDOM = require("react-dom");

class App extends React.Component {
    render() {
        return (
            <div>
                <FormArea />
                <PlayerList />
            </div>
        );
    }
}

class PlayerList extends React.Component {
    render() {
        return <h1>Active players</h1>;
    }
}

class FormArea extends React.Component {
    constructor(props) {
        super(props);

        // Players are sorted by id - their key is their ID
        this.state = { players: { byId: {}, allIds: [] } };

        this.addPlayer = this.addPlayer.bind(this);
        this.removePlayer = this.removePlayer.bind(this);
        this.startGame = this.startGame.bind(this);
    }

    addPlayer(event) {
        event.preventDefault();

        let activeIDs = this.state.players.allIds;

        let playerName = document.getElementById("playerNameInput").value;
        let kitNumber = document.getElementById("kitNumberSelectAdd").value;

        if (!activeIDs.includes(kitNumber)) {
            activeIDs.push(kitNumber);

            wsClient.send(
                JSON.stringify({
                    action: "addPlayer",
                    name: playerName,
                    id: kitNumber
                })
            );

            // Update state. Must be done immutably, hence the spread operators
            this.setState({
                ...this.state,
                players: {
                    ...this.state.players,
                    byId: {
                        ...this.state.players.byId,
                        [kitNumber]: {
                            id: kitNumber,
                            name: playerName
                        }
                    },
                    allIds: activeIDs
                }
            });
        } else {
            console.log(
                "Player with ID " + kitNumber + " is already in the game"
            );
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

            wsClient.send(
                JSON.stringify({
                    action: "removePlayer",
                    id: kitNumber
                })
            );

            // Extract deleted player from state
            let {
                [kitNumber]: deleted,
                ...remainingPlayers
            } = this.state.players.byId;

            console.log(remainingPlayers);

            // Update state immutably
            this.setState({
                ...this.state,
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
                wsClient.send(
                    JSON.stringify({
                        action: "startGame",
                        time: gametime
                    })
                );
            } else {
                console.log("Not enough players");
            }
        } else {
            console.log("Gametime not withing range");
        }
    }

    render() {
        // Fill array of all possible kit numbers
        let numbers = new Array(10);
        for (i = 0; i < 10; i++) {
            numbers = <option value={id}>#{id}</option>;
        }

        const activeIDs = this.state.players.allIds.map(id => {
            <option value={id}>
                #{id} - {this.state.players.byId[id]}
            </option>;
        });

        return (
            <div className="container">
                <div className="row">
                    <div className="col">
                        <h4>Add player</h4>
                        <form onSubmit={this.addPlayer} id="addPlayerForm">
                            <div className="form-group">
                                <label>Player: </label>
                                <input
                                    className="form-control"
                                    type="text"
                                    name="name"
                                    id="playerNameInput"
                                    placeholder="Name of player"
                                />
                            </div>

                            <div className="form-group">
                                <label>Kit ID:</label>
                                <select
                                    className="form-control"
                                    name="kitnumber"
                                    id="kitNumberSelectAdd"
                                >
                                    <option value="1">1</option>
                                    <option value="2">2</option>
                                    <option value="3">3</option>
                                    <option value="4">4</option>
                                    <option value="5">5</option>
                                    <option value="6">6</option>
                                    <option value="7">7</option>
                                    <option value="8">8</option>
                                    <option value="9">9</option>
                                    <option value="10">10</option>
                                </select>
                            </div>

                            <input
                                type="submit"
                                className="btn btn-primary"
                                value="Add player"
                            />
                        </form>
                    </div>

                    <div className="col">
                        <h4>Remove player</h4>
                        <form
                            onSubmit={this.removePlayer}
                            id="removePlayerForm"
                        >
                            <div className="form-group">
                                <label>Kit ID:</label>
                                <select
                                    className="form-control"
                                    name="kitnumber"
                                    id="kitNumberSelectRemove"
                                >
                                    <option value="1">1</option>
                                    <option value="2">2</option>
                                    <option value="3">3</option>
                                    <option value="4">4</option>
                                    <option value="5">5</option>
                                    <option value="6">6</option>
                                    <option value="7">7</option>
                                    <option value="8">8</option>
                                    <option value="9">9</option>
                                    <option value="10">10</option>
                                </select>
                            </div>

                            <input
                                type="submit"
                                className="btn btn-primary"
                                value="Remove player"
                            />
                        </form>
                    </div>

                    <div className="col start-game-form">
                        <h4>Start game</h4>
                        <form className="align-bottom">
                            <div className="form-group">
                                <label>Gametime: </label>
                                <input
                                    type="number"
                                    min="10"
                                    max="20"
                                    id="gametime"
                                    placeholder="Enter the gametime..."
                                />
                            </div>
                            <button
                                className="btn btn-primary"
                                onClick={this.startGame}
                            >
                                Start game
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        );
    }
}

ReactDOM.render(<App />, document.getElementById("react-container"));
