import { CollectionsApi } from './client'
import { bindNodeCallback, of, zip } from 'rxjs';
import Table from 'cli-table';
import { map, mergeMap } from 'rxjs/operators';
import { errorException, errorStyle, infoStyle } from './styles';
import chalk from 'chalk';
import { databaseNotDefined, launchEditor, obsCollectionNotDefined, obsDatabaseNotDefined } from './commands';
import { Option } from './InfiniteCommander';

const collectionsApi = new CollectionsApi();
const collections = bindNodeCallback(collectionsApi.collections);
const getCollectionByName = bindNodeCallback(collectionsApi.getCollectionByName);
const addData = bindNodeCallback(collectionsApi.addData);
const insertDatas = bindNodeCallback(collectionsApi.insertDatas);
const getDatas = bindNodeCallback(collectionsApi.getDatas);
const count = bindNodeCallback(collectionsApi.count);
const createCollection = bindNodeCallback(collectionsApi.createCollection);
const getCollectionAvg = bindNodeCallback(collectionsApi.getCollectionAvg);


export default function (context, subscriber) {
    return (program) => {
        program
            .command('use-coll <useColl>')
            .description('Define the collection to use')
            .action((useColl) => {
                if (!useColl || useColl.length === 0) {
                    return;
                }
                of(context.database)
                    .pipe(
                        databaseNotDefined,
                        mergeMap(db => getCollectionByName(db, useColl))
                    )
                    .subscribe(() => {
                        context.setCollection(useColl);
                        subscriber.complete();
                    }, err => {
                        console.log(errorStyle(`no collection '${useColl}' in ${context.database}`));
                        subscriber.complete();
                    });

            });

        program
            .command('create-coll <useColl>')
            .description('Create an collection with an specific name')
            .action((useColl) => {
                if (!useColl || useColl.length === 0) {
                    return;
                }
                of(context.database)
                    .pipe(
                        databaseNotDefined,
                        mergeMap(d => createCollection(d, useColl))
                    )
                    .subscribe(thor => {
                        console.log(`${thor.name} loaded.`);
                        subscriber.complete();
                    }, errorException(subscriber));
            });

        program
            .command('list-coll')
            .description('List all collections')
            .action(() => {

                of(context.database)
                    .pipe(
                        databaseNotDefined,
                        mergeMap(d => collections(d))
                    )
                    .subscribe(list => {
                        if (!list || list.length === 0) {
                            console.log(chalk.italic('<empty>'));
                        } else {
                            const table = new Table({ head: ['name'] });
                            const results = list.map(({ name }) => [name]);
                            table.push(results);
                            console.log(infoStyle(table.toString()));
                        }
                        subscriber.complete();
                    }, errorException(subscriber));
            });

        program
            .command('add')
            .description('add an json object to the collection')
            .action(() => {

                zip(obsDatabaseNotDefined(context), obsCollectionNotDefined(context), launchEditor)
                    .pipe(
                        map(([database, collection, objTxt]) => ({ database, collection, objTxt })),
                        map(c => {
                            c.obj = JSON.parse(c.objTxt);
                            return c;
                        }),
                        mergeMap(c => addData(c.database, c.collection, c.obj))
                    )
                    .subscribe(data => {
                        console.log(infoStyle(`Data added : ${JSON.stringify(data)}`));
                        subscriber.complete();
                    }, errorException(subscriber))
            });

        program
            .command('insert')
            .description('insert an array of Json Objects to the collection')
            .action(() => {

                zip(
                    obsDatabaseNotDefined(context),
                    obsCollectionNotDefined(context),
                    launchEditor
                )
                    .pipe(
                        map(([database, collection, objTxt]) => ({ database, collection, objTxt })),
                        map(c => {
                            c.obj = JSON.parse(c.objTxt);
                            return c;
                        }),
                        mergeMap(c => insertDatas(c.database, c.collection, c.obj))
                    )
                    .subscribe(data => {
                        console.log(infoStyle(`Data inserted : ${JSON.stringify(data)}`));
                        subscriber.complete();
                    }, errorException(subscriber))
            });
        const limit = new Option('-l, --limit [limit]', 'Limit the number of results').default(10);
        const skip = new Option('-s, --skip [skip]', 'Skip an number of results').default(0);
        program
            .command('datas')
            .description('view all datas')
            .addOption(limit)
            .addOption(skip)
            .action((options) => {

                zip(obsDatabaseNotDefined(context), obsCollectionNotDefined(context))
                    .pipe(
                        mergeMap(([database, collection]) => getDatas(database, collection))
                    )
                    .subscribe(datas => {
                        const table = new Table(
                            { head: ['id', 'content'] }
                        );
                        const results = datas.map((obj) => [obj.$loki, JSON.stringify(obj)]);
                        results.forEach(r => table.push(r));
                        console.log(table.toString());
                        subscriber.complete();
                    }, errorException(subscriber))
            });
        program
            .command('count')
            .description('count the number of data in this collection')
            .action(() => {

                zip(obsDatabaseNotDefined(context), obsCollectionNotDefined(context))
                    .pipe(
                        mergeMap(([database, collection]) => count(database, collection))
                    )
                    .subscribe(nb => {
                        const table = new Table(
                            { head: ['Size'] }
                        );
                        table.push([nb]);
                        console.log(table.toString());
                        subscriber.complete();
                    }, errorException(subscriber))
            });
        program
            .command('avg <property>')
            .description('avg on property')
            .action((property) => {

                zip(obsDatabaseNotDefined(context), obsCollectionNotDefined(context))
                    .pipe(
                        mergeMap(([database, collection]) => getCollectionAvg(database, collection, property))
                    )
                    .subscribe(nb => {
                        const table = new Table(
                            { head: ['Avg'] }
                        );
                        table.push([nb]);
                        console.log(table.toString());
                        subscriber.complete();
                    }, errorException(subscriber))
            });
    };
}