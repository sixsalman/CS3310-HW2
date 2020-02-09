package hw2cs3310_Khan;

/**
 * Objects of this class hold and link information in a (linked) list
 */
class Node {

    // Point #3
    char nucleotide;
    Node prev;
    Node next;

    /**
     * Creates a Node object, assigning the received values to its fields
     * @param nucleotide receives a character representing a base in a DNA/RNA
     * @param prev receives reference to another node
     * @param next receives reference to another node
     */
    Node(char nucleotide, Node prev, Node next) {
        this.nucleotide = nucleotide;
        this.prev = prev;
        this.next = next;
    }

}
