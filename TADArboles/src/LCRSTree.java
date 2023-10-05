import material.Position;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * An implementation of the NAryTree interface using left-child, right-sibling representation.
 *
 * @param <E> the type of elements stored in the tree
 */
public class LCRSTree<E> implements NAryTree<E> {

    private LCRSnode<E> root;
    private int size;

    private class LCRSnode<T> implements Position<T> {

        private T element;
        private LCRSnode<T> parent;
        private LCRSnode<T> child;
        private LCRSnode<T> sibling;

        public LCRSnode(T element, LCRSnode<T> parent, LCRSnode<T> child, LCRSnode<T> sibling) {
            this.element = element;
            this.parent = parent;
            this.child = child;
            this.sibling = sibling;
        }

        public LCRSnode(T element) {
            this(element, null, null, null);
        }

        public LCRSnode(T element, LCRSnode<T> parent) {
            this(element, parent, null, null);
        }

        @Override
        public T getElement() {
            return element;
        }

        public LCRSnode<T> getParent() {
            return parent;
        }

        public LCRSnode<T> getChild() {
            return child;
        }

        public LCRSnode<T> getSibling() {
            return sibling;
        }

        public void setElement(T element) {
            this.element = element;
        }

        public void setParent(LCRSnode<T> parent) {
            this.parent = parent;
        }

        public void setChild(LCRSnode<T> child) {
            this.child = child;
        }

        public void setSibling(LCRSnode<T> sibling) {
            this.sibling = sibling;
        }
    }

    private LCRSnode<E> checkPosition(Position<E> p) {
        if (!(p instanceof LCRSnode)) {
            throw new RuntimeException("La posición es inválida.");
        }
        return (LCRSnode<E>) p;
    }

    @Override
    public Position<E> addRoot(E e) {
        if (!isEmpty()) {
            throw new RuntimeException("El árbol ya tiene raíz.");
        }
        this.root = new LCRSnode<>(e);
        this.size = 1;
        return this.root;
    }

    @Override
    public Position<E> add(E element, Position<E> p) {
        LCRSnode<E> parent = checkPosition(p);
        LCRSnode<E> newNode = new LCRSnode<>(element, parent);
        if (parent.getChild() == null) {
            parent.setChild(newNode);
        } else {
            LCRSnode<E> leftChild = parent.getChild();
            while (leftChild.getSibling() != null) {
                leftChild = leftChild.getSibling();
            }
            leftChild.setSibling(newNode);
        }
        this.size++;
        return newNode;
    }

    @Override
    public Position<E> add(E element, Position<E> p, int n) {
        LCRSnode<E> parent = checkPosition(p);
        LCRSnode<E> newNode = new LCRSnode<>(element, parent);
        if (n < 0) {
            throw new RuntimeException("La posición es inválida.");
        } else if (n == 0) {
            newNode.setSibling(parent.getChild());
            parent.setChild(newNode);
        } else {
            LCRSnode<E> leftChild = parent.getChild();
            int i = 1;
            while (i < n && leftChild.getSibling() != null) {
                leftChild = leftChild.getSibling();
                i++;
            }
            newNode.setSibling(leftChild.getSibling());
            leftChild.setSibling(newNode);
        }
        this.size++;
        return newNode;
    }

    @Override
    public void swapElements(Position<E> p1, Position<E> p2) {
        var node1 = checkPosition(p1);
        var node2 = checkPosition(p2);
        E aux = node1.getElement();
        node1.setElement(node2.getElement());
        node2.setElement(aux);
    }

    @Override
    public E replace(Position<E> p, E e) {
        var node = checkPosition(p);
        E old = node.getElement();
        node.setElement(e);
        return old;
    }

    private int computeSize(LCRSnode<E> node) {
        if (node == null) {
            return 0;
        } else {
            int size = 1;
            LCRSnode<E> child = node.getChild();
            while (child != null) {
                size += computeSize(child);
                child = child.getSibling();
            }
            return size;
        }
    }

    @Override
    public void remove(Position<E> p) {
        LCRSnode<E> node = checkPosition(p);
        if (node == root) {
            root = null;
            size = 0;
        } else {
            LCRSnode<E> parent = node.getParent();
            if (parent.getChild() == node) {
                parent.setChild(node.getSibling());
            } else {
                LCRSnode<E> leftChild = parent.getChild();
                while (leftChild.getSibling() != node) {
                    leftChild = leftChild.getSibling();
                }
                leftChild.setSibling(node.getSibling());
            }
            size -= computeSize(node);
        }
    }

    @Override
    public NAryTree<E> subTree(Position<E> v) {
        var node = checkPosition(v);
        var tree = new LCRSTree<E>();
        tree.root = node;
        tree.size = computeSize(node);
        return tree;
    }

    @Override
    public void attach(Position<E> p, NAryTree<E> t) {
        var node = checkPosition(p);
        var tree = (LCRSTree<E>) t;
        var leftChild = node.getChild();
        if (leftChild == null) {
            node.setChild(tree.root);
        } else {
            while (leftChild.getSibling() != null) {
                leftChild = leftChild.getSibling();
            }
            leftChild.setSibling(tree.root);
        }
        this.size += tree.size;
    }

    @Override
    public boolean isEmpty() {
        return this.root == null;
    }

    @Override
    public Position<E> root() {
        return this.root;
    }

    @Override
    public Position<E> parent(Position<E> v) {
        var node = checkPosition(v);
        return node.getParent();
    }

    @Override
    public Iterable<? extends Position<E>> children(Position<E> v) {
        var node = checkPosition(v);
        var list = new ArrayList<Position<E>>();
        var child = node.getChild();
        while (child != null) {
            list.add(child);
            child = child.getSibling();
        }
        return list;
    }

    @Override
    public boolean isInternal(Position<E> v) {
        var node = checkPosition(v);
        return node.getChild() != null;
    }

    @Override
    public boolean isLeaf(Position<E> v) {
        var node = checkPosition(v);
        return node.getChild() == null;
    }

    @Override
    public boolean isRoot(Position<E> v) {
        var node = checkPosition(v);
        return node == this.root;
    }

    @Override
    public Iterator<Position<E>> iterator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int size() {
        return this.size;
    }

}
