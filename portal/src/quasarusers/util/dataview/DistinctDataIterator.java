package quasarusers.util.dataview;

import com.sdm.quasar.dataview.server.DataIterator;
import com.sdm.quasar.dataview.server.model.QueryModel;
import com.sdm.quasar.lang.Keywords;

/**
 * Dies ist ein Dateniterator für die DataView, der Dupliake eliminiert.<p>
 *
 * <i>Achtung</i>: Normalerweise is es besser, Duplikate durch ein "select distinct"
 *                 zu beheben.
 */
public final class DistinctDataIterator implements DataIterator {
    private DataIterator dataIterator;
    private Object[] nextInput = null;
    private Object[] currentInput = null;

    public DistinctDataIterator(QueryModel queryModel, Keywords arguments) {
        this.dataIterator = new PoolDataIterator(queryModel, arguments);
    }

    public DataIterator getDataIterator() {
        return dataIterator;
    }

    protected Object[] getNextInput() {
        return nextInput;
    }

    protected Object[] getCurrentInput() {
        return currentInput;
    }

    protected Object[] produceNextInput() {
        currentInput = nextInput;

        if (dataIterator.hasNext())
            return nextInput = (Object[])dataIterator.next();
        else
            return nextInput = null;
    }

    protected boolean hasNextInput() {
        return getNextInput() != null;
    }

    public QueryModel getQueryModel() {
        return dataIterator.getQueryModel();
    }

    public void start() throws Exception {
        dataIterator.start();

        if (dataIterator.hasNextResult())
            produceNextInput();
    }

    public void release() {
        dataIterator.release();
    }

    public boolean hasNextResult() throws Exception {
        return hasNextInput();
    }

    public boolean hasNext() {
        return hasNextInput();
    }

    public void remove() {
        dataIterator.remove();
    }

    public Object next() {
        try {
            return nextResult();
        }
        catch (Exception e) {
            return null;
        }
    }

    public void setMaximumSize(int maximumSize) {
//        dataIterator.setMaximumSize(maximumSize);
    }

    public int getGroupIndex() {
        return 0;
    }

    private Object getNextInputGroup() {
        if (getNextInput() != null)
            return getNextInput()[getGroupIndex()];
        else
            return null;
    }

    public Object nextResult() throws Exception {
        produceNextInput();

        while (getCurrentInput()[getGroupIndex()].equals(getNextInputGroup())) {
            produceNextInput();
        }

        return getCurrentInput();
    }
}
