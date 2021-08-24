import chalk from 'chalk';

export const errorStyle = function (msg) {
    return chalk.red.bold(msg);
};

export const infoStyle = function (msg) {
    return chalk.blue(msg);
};

const extractResponse = (err) =>{
    if(err && err.response && err.response.body  && err.response.body.error){
        return err.response.body.error;
    }
    return err;
}


export const errorException = subscriber => err => {
    const msg = extractResponse(err);
    console.log(errorStyle(msg));
    subscriber.complete();
};
