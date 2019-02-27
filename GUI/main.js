const electron = require('electron');
const url = require('url');
const path = require('path');

const {app, BrowserWindow, Menu, ipcMain} = electron;

let mainWindow;

// Listen for app to be ready
app.on('ready', 
    () => {   //create window
        mainWindow = new BrowserWindow();
        // Load html
        mainWindow.loadURL(url.format({
            pathname: path.join(__dirname, 'main_window.html'),
            protocol: 'file',
            slashes: true
        })); //passes this into loadURL:    file://dirname/main_window.html
    
        //Quit entire app on close
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
            }
        ]
    }
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