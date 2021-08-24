import { Command } from './InfiniteCommander';
import databaseCommands from './database-commands';
import collectionCommands from './collection-commands';
import { Observable } from 'rxjs';
import { infoStyle } from './styles';

export default function (context, argv) {
    return new Observable(subscriber => {
        const program = new Command();
        program.version('0.0.1')

        databaseCommands(context, subscriber)(program);
        collectionCommands(context, subscriber)(program);
        program
            .command('quit')
            .description('Quit the shell')
            .action(() => {
                console.log(infoStyle('Process exit.'));
                process.exit(0);
            });
        program.parse(argv);
        if(program.isHelp && !subscriber.closed){
            subscriber.complete();
        }
    });


}