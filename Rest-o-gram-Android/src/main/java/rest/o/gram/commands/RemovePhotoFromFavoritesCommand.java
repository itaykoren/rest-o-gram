package rest.o.gram.commands;

import org.json.rpc.client.HttpJsonRpcClientTransport;
import rest.o.gram.tasks.ITaskObserver;
import rest.o.gram.tasks.RemovePhotoFromFavoritesTask;

/**
 * Created with IntelliJ IDEA.
 * User: Itay
 * Date: 14/06/13
 */
public class RemovePhotoFromFavoritesCommand extends AsyncTaskRestogramCommand {

    public RemovePhotoFromFavoritesCommand(HttpJsonRpcClientTransport transport, ITaskObserver observer, String photoId) {
        super(transport, observer);
        this.photoId = photoId;
    }

    @Override
    public boolean execute() {
        if (!super.execute())
            return false;

        RemovePhotoFromFavoritesTask t = new RemovePhotoFromFavoritesTask(transport, this);

        t.execute(photoId);
        task = t;

        return true;
    }


    private String photoId;

}