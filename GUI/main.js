const electron = require("electron");
const url = require("url");
const os = require("os");
const path = require("path");

const { app, BrowserWindow, Menu, ipcMain, globalShortcut } = electron;

let mainWindow;
let menuOverlay;
let menuExists = false;

// Listen for app to be ready
app.on("ready", () => {
    //create main window
    mainWindow = new BrowserWindow({
        show: false,
        frame: false,
        // resizable: false,
        // height: 560,
        // width: 700,
        title: "Laser Tag-3000"
    });
    // Load html
    mainWindow.loadURL(
        url.format({
            pathname: path.join(__dirname, "windows/main_window.html"),
            protocol: "file",
            slashes: true
        })
    ); //passes this into loadURL:    file://dirname/windows/main_window.html

    mainWindow.once("ready-to-show", () => {
        mainWindow.maximize();
        mainWindow.show();
    });

    globalShortcut.register("Esc", toggleMenuOverlay);

    //Quit entire app on closed
    mainWindow.on("closed", () => {
        app.quit();
    });

    const mainMenu = Menu.buildFromTemplate(mainMenuTemplate);
    Menu.setApplicationMenu(mainMenu);

    // React Developer Tools
    if (process.env.NODE_ENV != "production") {
        BrowserWindow.addDevToolsExtension(
            path.join(
                os.homedir(),
                "/.config/google-chrome/Default/Extensions/fmkadmapgofadopljbjfkapdkoienihi/3.6.0_0"
            )
        );
    }
});

/* ~~~~~~ TOGGLE MENU OVERLAY ~~~~~~ */
function toggleMenuOverlay(err) {
    if (menuExists == false) {
        menuOverlay = new BrowserWindow({
            parent: mainWindow,
            modal: true,
            show: false,
            backgroundColor: "#cbccf3",
            frame: false,
            width: 300,
            height: 200,
            skipTaskbar: true
            //resizable: false,
        });

        menuExists = true;

        menuOverlay.loadURL(`file://${__dirname}/windows/menu_overlay.html`); //backticks so I can use var's
        menuOverlay.once("ready-to-show", () => {
            menuOverlay.show();
        });

        menuOverlay.once("closed", () => {
            menuExists = false;
            menuOverlay = null;
        });
    } else {
        menuOverlay.close();
    }
}

/* ================================= */
/* =======    Catch Events  ======== */
/* ================================= */

// Catch 'add players'
ipcMain.on("addPlayer", (event, msg) => {
    mainWindow.webContents.send("sendToServer", {
        action: "addPlayer",
        ...msg
    });

    menuOverlay.close(); //close menu
});

// Catch 'remove players'
ipcMain.on("removePlayer", (event, msg) => {
    console.log(msg);

    mainWindow.webContents.send("sendToServer", {
        action: "removePlayer",
        ...msg
    });

    menuOverlay.close(); //close menu
});

// Catch 'stop game'
ipcMain.on("stopGame", event => {
    mainWindow.webContents.send("sendToServer", {
        action: "stopGame"
    });

    mainWindow.webContents.send("stopGame");

    if (menuExists) {
        menuOverlay.close(); //close menu
    }
});

// Catch 'start game'
ipcMain.on("startGame", (event, msg) => {
    mainWindow.webContents.send("sendToServer", {
        action: "startGame",
        ...msg
    });

    menuOverlay.close(); //close menu
});

// Catch quit
ipcMain.on("quit", event => {
    mainWindow.webContents.send("sendToServer", {
        action: "stopGame"
    });

    app.quit();
});

/* ============================ */
/* ====== Menu template ======= */
/* ============================ */

// Create menu template (top of the app window)
const mainMenuTemplate = [
    {
        label: "File",
        submenu: [
            {
                label: "Save highscores",
                click() {
                    createSaveHighscoreWindow();
                }
            },
            {
                label: "Clear highscores",
                click() {
                    mainWindow.webContents.send("item:clear");
                }
            },
            {
                label: "Exit",
                accelerator: "CmdOrCtrl+Q",
                click() {
                    app.quit();
                }
            },
            {
                role: "close"
            }
        ]
    },
    {
        role: "edit"
    }
];

// If Mac, add empty object to menu, because Mac is stupid
if (process.platform == "darwin") {
    mainMenuTemplate.unshift({}); //adds empty to beginning of array
}

// Add developer tools if not in production mode
if (process.env.NODE_ENV != "production") {
    mainMenuTemplate.push({
        label: "Developer tools",
        submenu: [
            {
                label: "Toggle devtools",
                accelerator: "CmdOrCtrl+i",
                click(item, focusedWindow) {
                    focusedWindow.toggleDevTools(); //chrome dev tools
                }
            },
            {
                role: "reload" //default reload
            }
        ]
    });
}
