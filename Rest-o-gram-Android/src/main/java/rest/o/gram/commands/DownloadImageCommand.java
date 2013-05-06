package rest.o.gram.commands;

import android.widget.ImageView;
import rest.o.gram.tasks.DownloadImageTask;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 06/05/13
 */
public class DownloadImageCommand extends AbstractRestogramCommand {

    public DownloadImageCommand(String url, ImageView imageView) {
        this.url = url;
        this.imageView = imageView;
        this.width = -1;
        this.height = -1;
    }

    public DownloadImageCommand(String url, ImageView imageView, int width, int height) {
        this(url, imageView);
        this.width = width;
        this.height = height;
    }

    @Override
    public void execute() {
        super.execute();

        DownloadImageTask task;
        if(width == -1 && height == -1)
            task = new DownloadImageTask(imageView);
        else
            task = new DownloadImageTask(imageView, width, height);

        task.setContext(this);
        task.execute(url);
    }

    /**
     * Called after task has finished executing
     */
    public void taskFinished() {
        notifyFinished();
    }

    /**
     * Called after task has encountered an error
     */
    public void taskError() {
        notifyError();
    }

    private String url;
    private ImageView imageView;
    private int width;
    private int height;
}
