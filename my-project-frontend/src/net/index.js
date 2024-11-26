import axios from "axios";
import {ElMessage} from "element-plus";

const authItemName = "authorize"

const accessHeader = () => {
    return {
        'Authorization': `Bearer ${takeAccessToken()}`
    }
}

const defaultError = (error) => {
    console.error(error); // 打印完整错误信息，方便调试

    // 获取错误状态码和后端返回的信息
    const statusCode = error.response?.status; // HTTP状态码
    const errorMessage = error.response?.data?.message || 'Unknown error occurred'; // 后端返回的错误信息

    // 根据状态码显示不同提示信息
    let userMessage = 'Some errors have occurred, please contact the administrator!!!!!!!';

    switch (statusCode) {
        case 400:
            userMessage = `Bad Request: ${errorMessage || 'Please check your input.'}`;
            break;
        case 401:
            userMessage = 'Unauthorized: Please login first.';
            break;
        case 403:
            userMessage = 'Forbidden: You do not have permission to perform this action.';
            break;
        case 404:
            userMessage = `Not Found: The requested resource is not available. (${errorMessage})`;
            break;
        case 500:
            userMessage = 'Internal Server Error: Please contact the administrator.';
            break;
        default:
            userMessage = `Error: ${errorMessage || 'Unexpected error occurred.'}`;
            break;
    }

    // 显示提示框
    ElMessage.error(userMessage);
};

const defaultFailure = (message, status, url) => {
    console.warn(`The address of the request: ${url}, Status code: ${status}, error message: ${message}`)
    ElMessage.warning(message)
}

function takeAccessToken() {
    const str = localStorage.getItem(authItemName) || sessionStorage.getItem(authItemName);
    if(!str) return null
    const authObj = JSON.parse(str)
    if(new Date(authObj.expire) <= new Date()) {
        deleteAccessToken()
        ElMessage.warning("The login status has expired, please log in again!")
        return null
    }
    return authObj.token
}

function storeAccessToken(remember, token, expire){
    const authObj = {
        token: token,
        expire: expire
    }
    const str = JSON.stringify(authObj)
    if(remember)
        localStorage.setItem(authItemName, str)
    else
        sessionStorage.setItem(authItemName, str)
}

function deleteAccessToken() {
    localStorage.removeItem(authItemName)
    sessionStorage.removeItem(authItemName)
}

function internalPost(url, data, headers, success, failure, error = defaultError){
    axios.post(url, data, { headers: headers }).then(({data}) => {
        if(data.code === 200)
            success(data.data)
        else
            failure(data.message, data.code, url)
    }).catch(err => error(err))
}

function internalGet(url, headers, success, failure, error = defaultError){
    axios.get(url, { headers: headers }).then(({data}) => {
        if(data.code === 200)
            success(data.data)
        else
            failure(data.message, data.code, url)
    }).catch(err => error(err))
}

function login(username, password, remember, success, failure = defaultFailure){
    internalPost('/api/auth/login', {
        username: username,
        password: password
    }, {
        'Content-Type': 'application/x-www-form-urlencoded'
    }, (data) => {
        storeAccessToken(remember, data.token, data.expire)
        sessionStorage.setItem('username', data.username);
        ElMessage.success(`Login successful, welcome ${data.username} Come to our system`)
        success(data)
    }, failure)
}

function post(url, data, success, failure = defaultFailure) {
    internalPost(url, data, accessHeader() , success, failure)
}

function logout(success, failure = defaultFailure){
    get('/api/auth/logout', () => {
        deleteAccessToken()
        ElMessage.success(`The logout is successful, and you are welcome to use it again`)
        success()
    }, failure)
}

function get(url, success, failure = defaultFailure) {
    internalGet(url, accessHeader(), success, failure)
}

function unauthorized() {
    return !takeAccessToken()
}

export { post, get, login, logout, unauthorized }
