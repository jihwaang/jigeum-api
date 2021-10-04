const formPlan = document.querySelector('#form-plan');
const submitBtn = document.querySelector('#btn-submit');
let payload = {};
let userId = null;
const APP = {
    init: async function() {
        const fetchUserId = async (userId = null) => {
            userId = userId ?? '';
            let {id} = await fetch(`/users/${userId}`)
                .then(response => response.json())
                .then(result => result);
            return id === userId ? userId : id;
        }
        userId = await fetchUserId(localStorage.getItem('userId'));
        localStorage.setItem("userId", userId);
        document.querySelector('#host').value = userId;
        document.querySelector('#main').insertAdjacentHTML('beforeend', `<br /><div>your user id is ${userId}</div>`);
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
        const url = '/rooms';
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
        id = id ?? '6aadb021-ce19-46fd-95d9-36b3b5f86bbf';
        userId = userId ?? '8a7c9ca9-2c98-4311-a0e0-d11679681556';
        let requestURL = `/rooms/${id}`;
        let options = {
            method: 'POST',
            body: JSON.stringify({
                id: userId,
                name: '닉네임'
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
        const socket = new SockJS('/websocket');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, CHAT.onConnected, CHAT.onError)
    },
    onConnected: async function() {
        let messageBroker = '/room/6aadb021-ce19-46fd-95d9-36b3b5f86bbf';
        let publish = '/chat/send';
        stompClient.subscribe(messageBroker, CHAT.onMessageReceived);
        const result = await ROOM.fetchRoomInfo('6aadb021-ce19-46fd-95d9-36b3b5f86bbf', '8a7c9ca9-2c98-4311-a0e0-d11679681556');
        CHAT.showPreviousMessage(result);
        stompClient.send(publish, {}, JSON.stringify({
            type: 'ENTER',
            sender: '닉네임ㅋㅋ',//localStorage.getItem('userId'),
            content: '입장하셨습니다.',
            roomId:  '6aadb021-ce19-46fd-95d9-36b3b5f86bbf'
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
            sender: '닉네임ㅋㅋ',//localStorage.getItem('userId'),
            content: content,
              roomId:  '6aadb021-ce19-46fd-95d9-36b3b5f86bbf'
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
    APP.init();
    submitBtn.addEventListener('click', APP.createPlan);
    CHAT.connect(event);
    document.querySelector('#btn-send').addEventListener('click', (event) => {
       CHAT.sendMessage(event);
    });
    document.querySelector('#message').addEventListener('keyup', (event) => {
        if (event.key === 'Enter') document.querySelector('#btn-send').click();
    })
})

