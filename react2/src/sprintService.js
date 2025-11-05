const API_URL = 'http://localhost:8080/org/sprints';

function status(response) {
    console.log('Response status: ' + response.status);
    if (response.status >= 200 && response.status < 300) {
        return Promise.resolve(response);
    } else {
        return Promise.reject(new Error(response.statusText));
    }
}

function json(response) {
    return response.json();
}

export function GetAllSprints() {
    let headers = new Headers();
    headers.append('Accept', 'application/json');

    let requestConfig = { method: 'GET', headers: headers, mode: 'cors' };

    return fetch(API_URL, requestConfig)
        .then(status)
        .then(json)
        .then(data => {
            console.log('Request succeeded with JSON response', data);
            return data;
        })
        .catch(error => {
            console.log('Request failed', error);
            return Promise.reject(error);
        });
}

export function GetSprintById(id) {
    return fetch(`${API_URL}/${id}`, { method: 'GET', mode: 'cors' })
        .then(status)
        .then(json)
        .catch(error => {
            console.error('Error fetching sprint:', error);
            return Promise.reject(error);
        });
}

export function CreateSprint(sprint) {
    console.log('Creare sprint:', JSON.stringify(sprint));

    let headers = new Headers();
    headers.append('Accept', 'application/json');
    headers.append('Content-Type', 'application/json');

    let requestConfig = { method: 'POST', headers: headers, mode: 'cors', body: JSON.stringify(sprint) };

    return fetch(API_URL, requestConfig)
        .then(status)
        .then(json)
        .catch(error => {
            console.error('Error creating sprint:', error);
            return Promise.reject(error);
        });
}

export function UpdateSprint(id, sprint) {
    sprint.id = id;

    let headers = new Headers();
    headers.append('Accept', 'application/json');
    headers.append('Content-Type', 'application/json');

    let requestConfig = { method: 'PUT', headers: headers, mode: 'cors', body: JSON.stringify(sprint) };

    return fetch(`${API_URL}/${id}`, requestConfig)
        .then(status)
        .then(json)
        .catch(error => {
            console.error('Error updating sprint:', error);
            return Promise.reject(error);
        });
}

export function DeleteSprint(id) {
    console.log('Ștergere sprint:', id);

    return fetch(`${API_URL}/${id}`, { method: 'DELETE', mode: 'cors' })
        .then(status)
        .catch(error => {
            console.error('Error deleting sprint:', error);
            return Promise.reject(error);
        });
}
