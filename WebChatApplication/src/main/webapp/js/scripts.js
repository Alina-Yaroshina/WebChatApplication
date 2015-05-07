'use strict';

var change = false;
var object = document.parentNode;
var uniqueId = function() {
    var date = Date.now();
    var random = Math.random() * Math.random();

    return Math.floor(date * random).toString();
};

var newMessage = function(text, user, edited, deleted) {
    return {
        messageText: text,
        user: user,
        id: uniqueId(),
        deleted: deleted,
        edited: edited
    };
};

var appState = {
    username : '',
    mainUrl : 'chat',
    mesList:[],
    token : 'TN11EN',
    serverStatus: false
};

function run() {
    if(JSON.parse(localStorage.getItem("Chat username"))) {
        appState.username = JSON.parse(localStorage.getItem("Chat username"));
        document.getElementById('user').value = appState.username;
        document.getElementsByClassName('check')[0].style.visibility = 'visible';
    }

    var mesContainer = document.getElementsByClassName('working_place')[0];
    mesContainer.addEventListener('click', delegateEvent);
    restore();
}

function delegateEvent(evtObj) {
    if (evtObj.type == 'click' && evtObj.target.className == 'button_send' && !change) {
        if(!document.getElementById('user').value) {
            alert('Error! Input username first!')
            return;
        }
        document.getElementsByClassName('check')[0].style.visibility = 'visible';
        onAddButtonClick(evtObj);
    }
    if (evtObj.type == 'click' && evtObj.target.className == 'button_submit' ) {
        onSubmitButtonClick(evtObj);
    }
    if (evtObj.type == 'click' && evtObj.target.className == 'button_send' && change) {
        onEditSend(evtObj);
    }
    if (evtObj.type == 'click' && evtObj.target.nodeName == 'INPUT' && evtObj.target.id == 'user') {
        onUsernameChangeClick(evtObj);
    }
    if(evtObj.type == 'click' && evtObj.target.id == 'delete') {
        onDelete(evtObj);
    }
    if(evtObj.type == 'click' && evtObj.target.id == 'edit') {
        onMsgEdit(evtObj);
    }
}

function onDelete(obj) {
    change = false;
    obj.target.parentNode.parentNode.childNodes[1].textContent = "[deleted]";
    obj.target.parentNode.style.display = 'none';
    var id = obj.target.parentNode.parentNode.parentNode.attributes['data-mes-id'].value;
    for(var i = 0; i < appState.mesList.length; i++) {
        if(appState.mesList[i].id != id)
            continue;
        appState.mesList[i].message = "[deleted]";
        appState.mesList[i].deleted = true;
        del(appState.mainUrl + '?id=' + appState.mesList[i].id);
    }
}

function onEditSend() {
    var message = document.getElementById('message');
    if(message.value == '') return;
    var id = object.target.parentNode.parentNode.parentNode.attributes['data-mes-id'].value;
    for(var i = 0; i < appState.mesList.length; i++) {
        if(appState.mesList[i].id != id)
            continue;
        appState.mesList[i].message = message.value;
        object.target.parentNode.parentNode.childNodes[1].textContent = message.value;
        put(appState.mainUrl + '?id=' + appState.mesList[i].id, JSON.stringify(appState.mesList[i]));
        document.getElementById('message').value = appState.mesList[i].message;
    }
    change = false;
    document.getElementById('message').value = '';
}

function onMsgEdit(obj) {
    change = true;
    var message = document.getElementById('message');
    message.value = obj.target.parentNode.parentNode.childNodes[1].textContent;
    object = obj;
}

function onUsernameChangeClick() {
    var check = document.getElementsByClassName('check')[0];
    check.style.visibility = 'hidden';
}

function onSubmitButtonClick() {
    if (!document.getElementById('user').value) {
        return;
    }
    var check = document.getElementsByClassName('check')[0];
    check.style.visibility = 'visible';
    appState.username = document.getElementById('user').value;
    localStorage.setItem("Chat username", JSON.stringify(appState.username));
}
function onAddButtonClick() {
    var message = document.getElementById('message');
    var newMes = newMessage(message.value, appState.username);
    message.value = '';
    addMessage(newMes);
}

function addMessage(mesObj, continueWith) {
    //addMessageInternal(mesObj);
    post(appState.mainUrl, JSON.stringify(mesObj), function(){
        addMessageInternal(mesObj);
        continueWith && continueWith();
        restore();
    });
}

function addMessageInternal(mesObj) {
    if (!mesObj.messageText) {
        return;
    }
    var this_message = createMessage(mesObj);
    var message_box = document.getElementsByClassName('message_box')[0];
    appState.mesList.push(mesObj);
    message_box.appendChild(this_message);
    message_box.scrollTop = 9999;
}

function createMessage(message) {
    var divMessage = document.createElement('div');
    divMessage.setAttribute('data-mes-id', message.id);
    var oImg = document.createElement('img');
    var oText = document.createElement('div');
    var oName = document.createElement('div');
    oImg.setAttribute('src', 'https://img-fotki.yandex.ru/get/6822/307516655.0/0_17d3a8_7eb4eb0e_orig.png');
    oImg.setAttribute('height', '60px');
    oImg.setAttribute('width', '60px');
    oImg.style.marginLeft = '20px';
    oImg.style.marginTop = '25px';
    oImg.style.float = 'left';
    divMessage.classList.add('this_message');
    oText.classList.add('this_name');
    oText.classList.add('this_text');
    oText.appendChild(oName);
    oName.appendChild(document.createTextNode(message.user));
    oText.appendChild(document.createTextNode(message.messageText));
    oName.style.fontWeight = 'bold';
    oName.style.fontSize = '110%';
    oText.style.padding = '13px';
    oText.style.margin = '20px';
    if(!message.deleted) {
        var editDelete = document.createElement('div');
        editDelete.classList.add('edit_delete');
        var editImg = document.createElement('img');
        var deleteImg = document.createElement('img');
        editImg.setAttribute('src', 'http://www.sombrinha.net/studiocomciencia.com/images/pencil.png');
        editImg.setAttribute('height', '20px');
        editImg.setAttribute('width', '20px');
        editImg.setAttribute('id', 'edit');
        deleteImg.setAttribute('src', 'http://www.iconpng.com/png/symbolize/trash.png');
        deleteImg.setAttribute('height', '20px');
        deleteImg.setAttribute('width', '20px');
        deleteImg.setAttribute('id', 'delete');
        deleteImg.style.marginLeft = '5px';
        editImg.style.cursor = 'pointer';
        deleteImg.style.cursor = 'pointer';
        editDelete.style.marginTop = '10px';
        editDelete.appendChild(editImg);
        editDelete.appendChild(deleteImg);
        oText.appendChild(editDelete);
    }
    divMessage.appendChild(oImg);
    divMessage.appendChild(oText);
    return divMessage;
}

function restore(continueWith) {
    var url = appState.mainUrl + '?token=' + appState.token;
    get(url, function (responseText) {
        getHistory(responseText, function () {
            setTimeout(function () {
                restore(continueWith);
            }, 1000);
        });
    });
}

function getHistory(responseText, continueWith) {
    var response = JSON.parse(responseText);
    appState.token = response.token;
    createOrUpdateMessages(response.messages);
    continueWith && continueWith();
}

function createOrUpdateMessages(messages) {
    var mesBox = document.getElementsByClassName('message_box')[0];
    if (appState.mesList.length > 0) {
        for (var i = 0; i < messages.length; i++) {
            for (var i = 0; i < appState.mesList.length; i++) {
                if (appState.mesList[i].id == messages[i].id) {
                    continue;
                }
                else {
                    addMessage(messages[i]);
                }
            }
        }
    }
    else {
        for(var i = 0; i < messages.length; i++) {
            addMessageInternal(messages[i]);
        }
    }
}


function output(){
    document.getElementsByClassName('server_success')[0].style.display = 'none';
    document.getElementsByClassName('server_unsuccess')[0].style.display = 'inline';
    document.getElementsByClassName('server_unsuccess')[0].style.margin_top = '15px';
    document.getElementsByClassName('server_unsuccess')[0].style.float = 'right';
}

function defaultErrorHandler(message) {
    console.error(message);
    output(message);
}

function get(url, continueWith, continueWithError) {
    ajax('GET', url, null, continueWith, continueWithError);
}

function post(url, data, continueWith, continueWithError) {
    ajax('POST', url, data, continueWith, continueWithError);
}

function put(url, data, continueWith, continueWithError) {
    ajax('PUT', url, data, continueWith, continueWithError);
}

function del(url, data, continueWith, continueWithError) {
    ajax('DELETE', url, data, continueWith, continueWithError);
}

function isError(text) {
    if(text == "")
        return false;

    try {
        var obj = JSON.parse(text);
    } catch(ex) {
        return true;
    }

    return !!obj.error;
}

function ajax(method, url, data, continueWith, continueWithError) {
    var xhr = new XMLHttpRequest();

    continueWithError = continueWithError || defaultErrorHandler;
    xhr.open(method || 'GET', url, true);

    xhr.onload = function () {
        if (xhr.readyState !== 4)
            return;

        if(xhr.status != 200) {
            setServerStatus(method, false);
            continueWithError('Error on the server side, response ' + xhr.status);
            return;
        }

        if(isError(xhr.responseText)) {
            setServerStatus(method, false);
            continueWithError('Error on the server side, response ' + xhr.responseText);
            return;
        }
        setServerStatus(method, true);
        document.getElementsByClassName('server_unsuccess')[0].style.display = 'none';
        document.getElementsByClassName('server_success')[0].style.display = 'inline';
        document.getElementsByClassName('server_success')[0].style.margin_top = '15px';
        document.getElementsByClassName('server_success')[0].style.float = 'right';

        continueWith(xhr.responseText);
    };

    xhr.ontimeout = function () {
        setServerStatus(method, false);
        continueWithError('Server timed out !');
    }

    xhr.onerror = function (e) {
        var errMsg = 'Server connection error !\n'+
            '\n' +
            'Check if \n'+
            '- server is active\n'+
            '- server sends header "Access-Control-Allow-Origin:*"';
        setServerStatus(method, false);
        output(errMsg);
        continueWithError(errMsg);
    };

    xhr.send(data);
}

window.onerror = function(err) {
    output(err.toString());
}

function setServerStatus(method, serverState) {
    if (!appState.serverStatus && method != 'GET') {
        alert("Server is not available");
    }

    if (appState.serverStatus != serverState) {
        appState.serverStatus = serverState;
        if (serverState == true) {
            document.getElementsByClassName('server_unsuccess')[0].style.display = 'none';
            document.getElementsByClassName('server_success')[0].style.display = 'inline';
            document.getElementsByClassName('server_success')[0].style.margin_top = '15px';
            document.getElementsByClassName('server_success')[0].style.float = 'right';
        }
        else {
            document.getElementsByClassName('server_success')[0].style.display = 'none';
            document.getElementsByClassName('server_unsuccess')[0].style.display = 'inline';
            document.getElementsByClassName('server_unsuccess')[0].style.margin_top = '15px';
            document.getElementsByClassName('server_unsuccess')[0].style.float = 'right';
        }
    }
}