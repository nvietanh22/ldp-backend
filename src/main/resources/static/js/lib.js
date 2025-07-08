var lib = new lib();

function lib() {
    this.postApi = function (option) {
        if (option.beforePost) {
            option.beforePost();
        }
        $.post(option.url, option.data)
            .then(function (res) {
                lib.checkAut(res);
                if (option.complete) {
                    option.complete(res);
                }
            })
            .fail(function (err) {
                lib.checkAut(err);
                if (option.error) {
                    option.error(err);
                }
            });
    };

    this.post = function (option) {
        if (option.beforePost) {
            option.beforePost();
        }
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: option.url,
            data: option.data,
            headers: {
                "api-key": "Dejwak-mevped-8zoffi"
            },
            dataType: 'json',
            complete: function (response) {
                try {
                    debugger
                    if (response.responseJSON.rslt_msg === 'Success') {
                        if (option.complete) {
                            option.complete(response);
                        }
                    } else {
                        if (option.error) {
                            option.error(response);
                        }
                    }
                } catch (e) {
                    console.log(e);
                    alert('Lỗi hệ thống');
                }
            }
        });
    };
}



jQuery.fn.extend({
    getValue: function () {
        var result = {};
        var control = $(this).find(getClassInputForm());
        var controlName = [];
        for (let index = 0; index < control.length; index++) {
            var name = control[index].getAttribute('name');
            if (!controlName.find(x => x === name)) {
                controlName.push(name);
            }
        }
        for (const name of controlName) {
            var controls = $(this).find(getClassInputForm(name));
            var ctlValue = getValueControl(controls);
            if (ctlValue !== undefined) {
                result[name] = ctlValue;
            }
        }
        return result;
    },

})

getClassInputForm = function (name) {
    return name ?
        'input[name="' + name + '"],textarea[name="' + name + '"],select[name="' + name + '"]' :
        'input,textarea,select';
};

getValueControl = function (control) {
    var result;
    if (control.length === 1) {
        var type = control[0].getAttribute('type');
        switch (type) {
            case 'checkbox':
                result = control[0].checked ? true : false;
                break;
            case 'radio':
                result = control[0].checked ? true : false;
                break;
            case 'number':
                var valueInt = parseFloat(control[0].value);
                if (isNaN(valueInt)) {
                    valueInt = null;
                }
                result = valueInt;
                break;
            default:
                result = control.val();
                break;
        }
    } else if (control.length > 1) {
        var type = control[0].getAttribute('type');
        switch (type) {
            case 'checkbox':
                result = getValueControlChecked(control);
                break;
            case 'radio':
                result = getValueControlRadioChecked(control);
                break;
        }
    }
    return result;

    function getValueControlChecked(control) {
        var result = [];
        for (let index = 0; index < control.length; index++) {
            const item = control[index];
            if (item.checked) {
                result.push(item.value);
            }
        }
        return result;
    }

    function getValueControlRadioChecked(control) {
        var result = "0";
        for (var index = 0; index < control.length; index++) {
            var item = control[index];
            if (item.checked) {
                result = item.value;
            }
        }
        return result;
    }
};