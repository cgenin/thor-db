import { map } from 'rxjs/operators';
import { Observable, of } from 'rxjs';
import { prompt } from 'inquirer';

export const databaseNotDefined = map(d => {
    if (d) {
        return d;
    }
    throw new Error('Database not defined');
});

export const collectionNotDefined = map(c => {
    if (c) {
        return c;
    }
    throw new Error('Collection not defined');
});

export const obsDatabaseNotDefined = (context) =>{
    return of(context.database).pipe(
        databaseNotDefined
    );
};

export const obsCollectionNotDefined = (context) =>{
    return of(context.collection).pipe(
        collectionNotDefined
    );
};

export const launchEditor = new Observable(subs => {
    prompt([
        {
            type: 'editor',
            name: 'obj',
            message: 'type your json object : ',
        }
    ]).then(({ obj }) => {
        subs.next(obj);
        subs.complete();
    })
        .catch((err) => {
            subs.error(err);
            subs.complete();
        })
});
