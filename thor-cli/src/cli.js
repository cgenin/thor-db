import arg from 'arg';
import { ApiClient, } from './client';
import { Observable } from 'rxjs';
import ping from './ping';
import inquirer from 'inquirer';
import program from './program';
import { mergeMap, map } from 'rxjs/operators';
import { errorStyle, infoStyle } from './styles';


function parseArgumentsIntoOptions(rawArgs) {
    const args = arg(
        {
            '--url': String,
            '--verbose': Boolean,
            '-u': '--url',
        },
        {
            argv: rawArgs.slice(2),
        }
    );
    return {
        url: args['--url'] || 'http://127.0.0.1:8080',
        verbose: args['--verbose'] || false,
    };
}

class Context {
    constructor() {
        this.database = null;
        this.collection = null;
    }

    setDatabase(db) {
        this.database = db;
    }

    setCollection(c) {
        this.collection = c;
    }
}

const context = new Context();

const prompt = (questions) => new Observable(subscriber => {
    inquirer.prompt(questions)
        .then(v => {
            subscriber.next(v);
            subscriber.complete();
        }).catch(e => {
        subscriber.error(e);
        subscriber.complete();
    });
});


const terminal = () => {
    const message = infoStyle(`[${context.database || ''}][${context.collection || ''}]>`)
    prompt([{
        type: 'input',
        name: 'text',
        message,
        validate(v) {
            return v && v.length > 0;
        }
    }]).pipe(
        map(({ text }) => text.split(' ')),
        //map(arr => ['', '', ...arr]),
        mergeMap(argv => program(context, argv))
    )
        .subscribe(
            ()=>{
            },
            (err) => {
                console.error(err);
            }, terminal
        );
};

export function cli(args) {
    const options = parseArgumentsIntoOptions(args);
    ApiClient.instance.basePath = options.url;
    ping().subscribe(() => console.log(infoStyle('server is up')),
        e => {
            console.log(errorStyle(`Impossible to ping ${options.url}`))
            if (options.verbose) {
                console.log(e);
            }
            process.exit(1);
        }, terminal);
}