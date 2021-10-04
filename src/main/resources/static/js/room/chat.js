const formPlan = document.querySelector('#form-plan');
const submitBtn = document.querySelector('#btn-submit');
let payload = {};
let userId = null;
let roomId = null;
let sender = null;

const APP = {
    init: async function(event) {
        const fetchUserId = async (userId = null) => {
            userId = userId ?? '1';
            let {id} = await fetch(`http://localhost:8081/users/${userId}`)
                .then(response => response.json())
                .then(result => result);
            return id === userId ? userId : id;
        }
        userId = await fetchUserId(localStorage.getItem('userId'));
        localStorage.setItem("userId", userId);
        roomId = location.pathname.split("/")[2];
        CHAT.connect(event);
        //document.querySelector('#host').value = userId;
        //document.querySelector('#main').insertAdjacentHTML('beforeend', `<br /><div>your user id is ${userId}</div>`);
        // const elem = document.querySelector('input[name="placeAt"]');
        // const datepicker = new Datepicker(elem, {
        //
        // });
    },
    createPlan: async function() {
        const formData = new FormData(formPlan);
        formData.forEach((val, key) => payload[key] = val);
        console.log(JSON.stringify(payload));
        const year = document.querySelector('#year').value;
        const month = document.querySelector('#month').value;
        const date = document.querySelector('#date').value;
        const hour = document.querySelector('#hour').value;
        const minute = document.querySelector('#minute').value;
        payload.placeAt = APP.formatDate(year, month, date, hour, minute);
        const url = 'http://localhost:8081/rooms';
        const options = {
            method: 'POST',
            body: JSON.stringify(payload),
            headers: {
                'Content-Type': 'application/json; charset=utf-8'
            }
        }
        const result = await fetch(url, options)
            .then(response => response.json());
        console.log(result);
    }, formatDate: function(year, month, date, hour, minute) {
        const formatDate = `${year}-${month}-${date}T${hour}:${minute}:00`;
        return formatDate;
    }
}

const ROOM = {
    fetchRoomInfo: async function(id = null, userId = null) {
        id = id ?? '';
        userId = userId ?? '';
        let requestURL = `http://localhost:8081/rooms/${id}`;
        let options = {
            method: 'POST',
            body: JSON.stringify({
                id: userId,
                name: '닉네임입력받은값'
            }),
            headers: {
                'Content-Type': 'application/json; charset=utf-8'
            }
        }
        return await fetch(requestURL, options).then(response => response.json());
    }
}

const CHAT = {
    connect: function(event) {
        const socket = new SockJS('http://localhost:8081/websocket');
        stompClient = Stomp.over(socket);
        stompClient.connect({clientId: userId, roomId: roomId}, CHAT.onConnected, CHAT.onError)

    },
    onConnected: async function() {
        let messageBroker = `/room/${roomId}`;
        console.log('messageBroker::', messageBroker);
        let publish = '/chat/send';
        stompClient.subscribe(messageBroker, CHAT.onMessageReceived, {clientId: userId, roomId: roomId});
        const result = await ROOM.fetchRoomInfo(roomId, userId);
        console.log(result);
        // 닉네임가져오기
        let {users} = result;
        let {name} = users[userId];
        sender = name[roomId];

        CHAT.showPreviousMessage(result);
        stompClient.send(publish, {}, JSON.stringify({
             type: 'ENTER',
             sender: sender,//localStorage.getItem('userId'),
             content: '입장하셨습니다.',
             roomId:  roomId,
             senderId: userId
         }));
    },
    onMessageReceived: function({body}) {
        const message = JSON.parse(body);
        CHAT.showMessage(message);
    },
    sendMessage: function(event) {
        const content = document.querySelector('#message').value.trim();

        if (content && stompClient) {

            const message = {
                type: 'CONNECT',
                sender: sender,//localStorage.getItem('userId'),
                content: content,
                roomId:  roomId,
                senderId: userId
            };

            stompClient.send('/chat/send', {}, JSON.stringify(message));
            document.querySelector('#message').value = '';
        }

        event.preventDefault();
    },
    showMessage: function({type, latitude, longitude, content, sender, timestamp}) {
        const tmp = `<p class="message-content">${latitude}, ${longitude}</p>`;
        const messageHTML = `
            <div class="message">
                    <h3 class="username">${sender ?? ''}</h3>
                    <p class="message-content">${content ?? ''}</p>
                </div>
        `;
        document.querySelector('.chat-list').insertAdjacentHTML('beforeend', messageHTML);
        document.querySelector('.chat-list').scrollTop = document.querySelector('.chat-list').scrollHeight;
    },
    showPreviousMessage: function({conversation}) {
        if (!conversation) return;
        Array.from(conversation).map(message => {
            const {type, latitude, longitude, content, sender, timestamp} = message;
            const tmp = `<p class="message-content">${latitude ?? ''}, ${longitude ?? ''}</p>`;
            const messageHTML = `
            <div class="message">
                    <h3 class="username">${sender ?? ''}</h3>
                    <p class="message-content">${content ?? ''}</p>
                </div>
        `;
            document.querySelector('.chat-list').insertAdjacentHTML('beforeend', messageHTML);
            document.querySelector('.chat-list').scrollTop = document.querySelector('.chat-list').scrollHeight;
        });
    },
    onError: function(error) {
        console.log(error);
    }
}



window.addEventListener('DOMContentLoaded', (event) => {
    APP.init(event);
    //submitBtn.addEventListener('click', APP.createPlan);
    //CHAT.connect(event);
    document.querySelector('#btn-send').addEventListener('click', (event) => {
        CHAT.sendMessage(event);
    });
    document.querySelector('#message').addEventListener('keyup', (event) => {
        if (event.key === 'Enter') document.querySelector('#btn-send').click();
    })
})

