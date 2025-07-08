let btnSubmit = document.getElementById('btn-submit');
btnSubmit.addEventListener('click', () => {
    let formData = $('#formData').getValue();
    var navigator_info = window.navigator;
    var screen_info = window.screen;
    var deviceId = navigator_info.mimeTypes.length;
    deviceId += navigator_info.userAgent.replace(/\D+/g, '');
    deviceId += navigator_info.plugins.length;
    deviceId += screen_info.height || '';
    deviceId += screen_info.width || '';
    deviceId += screen_info.pixelDepth || '';

    let data = {
        request_id: uuidv4(),
        deviceId: deviceId,
        note: JSON.stringify(formData)
    }

    var currentURL = window.location.href;
    var domain = window.location.hostname;
    var port = window.location.port != '' ? `:${window.location.port}` : window.location.port;
    var protocol = window.location.protocol;
    let url = `${protocol}//${domain}${port}/api/lead/send`
    lib.post({
        url : url,
        data : JSON.stringify(data),
        complete : function(response) {
            console.log(response);
            alert('Submit data success');
        },
        error : function(ex) {
            alert('Submit data error');
            console.log(ex)
        }
    });
})


function uuidv4() {
    return "10000000-1000-4000-8000-100000000000".replace(/[018]/g, c =>
      (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
    );
  }