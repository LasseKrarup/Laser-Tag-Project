const electron = require('electron');
const url = require('url');
const path = require('path');

const {app, BrowserWindow, Menu, ipcMain, globalShortcut} = electron;

let mainWindow;
let menuOverlay;
let menuExists=false;

// Listen for app to be ready
app.on('ready', 
    () => {   //create window
        mainWindow = new BrowserWindow({
            show: false,
            frame: false,
            resizable: false,
            height: 560,
            width: 700,
            title: projectData.sysname,

        });
        // Load html
        mainWindow.loadURL(url.format({
            pathname: path.join(__dirname, 'main_window.html'),
            protocol: 'file',
            slashes: true
        })); //passes this into loadURL:    file://dirname/main_window.html
    
        mainWindow.once('ready-to-show', 
            () => {
                mainWindow.show();
            }
        );

        globalShortcut.register('Esc', toggleMenuOverlay);

        //Quit entire app on closed
        mainWindow.on('closed', ()=>{
                app.quit();
            }
        )

        const mainMenu = Menu.buildFromTemplate(mainMenuTemplate);
        Menu.setApplicationMenu(mainMenu);
    }
);

// Handle save highscore
function createSaveHighscoreWindow() {
    saveHighScoreWindow = new BrowserWindow({
        width: 300,
        height: 200,
        title: 'Really save high score?'
    });
    // Load html
    saveHighScoreWindow.loadURL(url.format({
        pathname: path.join(__dirname, 'saveHighScore_window.html'),
        protocol: 'file',
        slashes: true
    })); 
    
    saveHighScoreWindow.on('closed', () => {
        saveHighscoreWindow = null;
    })
}

// Catch item:saveHighscore
ipcMain.on('yes:saveHighscore',
    (e, item) =>{
        console.log(item);
        mainWindow.webContents.send('yes:saveHighscore', item);
        saveHighScoreWindow.close();
    }
);

// Catch 'add players'
ipcMain.on('addPlayers', 
    (event, msg) => {
        console.log(msg);

        menuOverlay.close(); //close menu
    }
)

// Catch 'remove players'
ipcMain.on('removePlayers', 
    (event, msg) => {
        console.log(msg);

        menuOverlay.close(); //close menu
    }
)

// Catch 'stop game'
ipcMain.on('stopGame', 
    (event, msg) => {
        console.log(msg);

        menuOverlay.close(); //close menu
    }
)

// Catch quit
ipcMain.on('quit', 
    (event) => {
        console.log('caught quit')
        app.quit();
    }
)

// Create menu template (top of the app window)
const mainMenuTemplate = 
[
    {
        label:'File',
        submenu:[
            {
                label: "Save highscores", 
                click(){
                    createSaveHighscoreWindow();
                }
            },
            {
                label: "Clear highscores",
                click(){
                    mainWindow.webContents.send('item:clear');
                }
            },
            {
                label: "Exit",
                accelerator: process.platform == 'darwin' ? 'Command+Q' : 'Ctrl+Q',
                click(){
                    app.quit();
                }
            },
            {
                role: 'close',
            }
        ]
    },
    {
        role: 'edit',
    },
];

// If Mac, add empty object to menu, because Mac is stupid
if(process.platform == 'darwin'){
    mainMenuTemplate.unshift({}); //adds empty to beginning of array
}

// Add developer tools if not in production mode
if(process.env.NODE_ENV != 'production'){
    mainMenuTemplate.push({
        label: "Developer tools",
        submenu: [
            {
                label: 'Toggle devtools',
                accelerator: process.platform == 'darwin' ? 'Command+i' : 'Ctrl+i',
                click(item, focusedWindow){
                    focusedWindow.toggleDevTools(); //chrome dev tools
                }
            },
            {
                role: 'reload' //default reload
            }
        ]
    });
}

function toggleMenuOverlay(err, ) {
    if (menuExists == false) {
            menuOverlay = new BrowserWindow({
            parent: mainWindow,
            modal: true,
            show: false,
            backgroundColor: "#cbccf3",
            frame: false,
            width: 300,
            height: 400,
            skipTaskbar: true,
            //resizable: false,
        })

        menuExists = true;

        menuOverlay.loadURL(`file://${__dirname}/menu_overlay.html`) //backticks so I can use var's
        menuOverlay.once('ready-to-show', () => {
            menuOverlay.show();
        })

        menuOverlay.once('closed', ()=>{
            menuExists = false;
            menuOverlay = null;
        })
    } else {
        menuOverlay.close();
    }
}


// Define global variables
global.projectData = {
    sysname: "Laser-Tag 3000"
}