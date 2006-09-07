This example demonstrates the Strategy design pattern.

Different sorting strategies are used to sort a list of numbers. If there are less than 10 numbers then bubblesort is used, if there less then 20 numbers then linearsort is used, otherwise quicksort is used. Quicksort divides the list in sublists. These sublists are sorted with bubblesort or linearsort if there are less than 20 elements.

SortingStrategy implements Strategy. BubbleSort, LinearSort, and QuickSort implement ConcreteStrategies. Sorter implements the Context.

The concern ForwardStrategy implements forwarding. Forwarding depends on the amount of numbers to sort and this is extracted by a meta filter from the argument to the sort method. Printing numbers before and after sorting is a crosscutting concern and implemented in concern ShowNumbers.
