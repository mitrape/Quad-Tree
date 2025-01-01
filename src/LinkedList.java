public class LinkedList<T> {
    private NodeList<T> head;

    public LinkedList() {
        head = null;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public int size() {
        int count = 0;
        NodeList<T> current = head;
        while (current != null) {
            count++;
            current = current.next;
        }
        return count;
    }

    public void add(T data) {
        NodeList<T> newNode = new NodeList<>(data);
        if (isEmpty()) {
            head = newNode;
        } else {
            NodeList<T> current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
    }

    public T get(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }
        NodeList<T> current = head;
        for (int i = 0; i < index; i++) {
            if (current == null) {
                throw new IndexOutOfBoundsException("Index out of bounds");
            }
            current = current.next;
        }
        if (current == null) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }
        return current.data;
    }
}
