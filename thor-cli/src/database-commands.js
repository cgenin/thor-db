import { DatabasesApi } from './client'
import { bindNodeCallback, of } from 'rxjs';
import Table from 'cli-table';
import { mergeMap } from 'rxjs/operators';
import { errorException, errorStyle, infoStyle } from './styles';
import { databaseNotDefined, obsDatabaseNotDefined } from './commands';

const databasesApi = new DatabasesApi();
const apiDatabasesGet = bindNodeCallback(databasesApi.apiDatabasesGet);
const apiDatabasesDatabaseNameGet = bindNodeCallback(databasesApi.apiDatabasesDatabaseNameGet);
const apiDatabasesDatabaseNameLoadPost = bindNodeCallback(databasesApi.apiDatabasesDatabaseNameLoadPost);
const apiDatabasesDatabaseNamePost = bindNodeCallback(databasesApi.apiDatabasesDatabaseNamePost);
const apiDatabasesDatabaseNameDelete = bindNodeCallback(databasesApi.apiDatabasesDatabaseNameDelete);
const apiDatabasesDatabaseNamePut = bindNodeCallback(databasesApi.apiDatabasesDatabaseNamePut);



export default function (context, subscriber) {
    return (program) => {
        program
            .command('use-db <useDb>')
            .description('Define the databaseCommands to use')
            .action((useDb) => {
                if (!useDb || useDb.length === 0) {
                    return;
                }
                context.setDatabase(useDb);
                context.setCollection(null);
                subscriber.complete();
            });
        program
            .command('load-db')
            .description('Load an database')
            .action(() => {
                obsDatabaseNotDefined(context)
                    .pipe(
                        mergeMap(d => apiDatabasesDatabaseNameLoadPost(d))
                    )
                    .subscribe(thor => {
                        console.log(`${thor.name} loaded.`);
                        subscriber.complete();
                    }, errorException(subscriber));
            });

        program
            .command('delete-db')
            .description('Delete an database')
            .action(() => {
                obsDatabaseNotDefined(context)
                    .pipe(
                        mergeMap(d => apiDatabasesDatabaseNameDelete(d))
                    )
                    .subscribe(thor => {
                        console.log(`${thor.name} deleted.`);
                        subscriber.complete();
                    }, errorException(subscriber));
            });
        program
            .command('save-db')
            .description('Save an database')
            .action(() => {


                obsDatabaseNotDefined(context)
                    .pipe(
                        mergeMap(d => apiDatabasesDatabaseNamePut(d))
                    )
                    .subscribe(thor => {
                        console.log(`${thor.name} saved.`);
                        subscriber.complete();
                    }, errorException(subscriber));

            });
        program
            .command('create-db')
            .description('Create an database')
            .action(() => {
                obsDatabaseNotDefined(context)
                    .pipe(
                        mergeMap(d => apiDatabasesDatabaseNamePost(d))
                    )
                    .subscribe(thor => {
                        console.log(`${thor.name} created.`);
                        subscriber.complete();
                    }, errorException(subscriber));
            });
        program
            .command('describe-db')
            .description('Show database attribute')
            .action(() => {
                obsDatabaseNotDefined(context)
                        .subscribe(thor => {
                            const table = new Table({ head: ['name', 'filename', 'engineVersion', 'databaseVersion', 'autosave', 'autosaveInterval'] });
                            const { name, filename, engineVersion, databaseVersion, autosave, autosaveInterval } = thor;
                            table.push([name, filename, engineVersion, databaseVersion, autosave, autosaveInterval]);
                            console.log(table.toString());
                            subscriber.complete();
                        }, errorException(subscriber));
            });
        program
            .command('list-db')
            .description('list all databases')
            .action(() => {
                apiDatabasesGet()
                    .subscribe((all) => {
                            all.forEach(o => {
                                console.log(infoStyle(`* ${o.name}`))
                            });
                            subscriber.complete();
                        },
                        errorException(subscriber)
                    );
            });
    };
}