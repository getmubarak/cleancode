
Chromosome – A particular permutation of a set of cities. For example if there are 3 cities and if we would index each city from 1 to 3, then the chromosomes/permutations can be expressed as these indices in different order like so: Chromosome 1 = [1,2,3], Chromosome 2 = [2,1,3] and so on.

Gene – Particular city in a set of cities. So a chromosome is composed of genes.

Fitness – Fitness is a measure that shows how strong/fit the chromosome is. For TSP the fitness can be simply the distance that is passed when all of the cities are visited. Shorter the distance, fitter the chromosome.

Crossover operation – Crossover is a process of taking some of the genes from one chromosome and the leftover genes from another. These genes are combined together to form a new chromosome

<img width="300" height="209" alt="image" src="https://github.com/user-attachments/assets/d1de552b-d91d-456c-a539-741c7b3abc29" />

The demo program uses the simple approach of picking one parent index from the top/best half of the population and the other parent index from the other half of the population. The next challenge is to combine half of the first parent with half of the second parent in a way that keeps characteristics of both parents but generates a legal solution where each city appears only once.The crossover point is the middle value in each parent. There are many more complicated ways to implement crossover, but the simple approach used in the demo is usually effective in my experience.

Mutation operation – Mutation is a process where some of the genes in a chromosome are altered. For example the index of two genes are swapped.



