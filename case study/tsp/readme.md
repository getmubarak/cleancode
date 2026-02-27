
Chromosome – A particular permutation of a set of cities. For example if there are 3 cities and if we would index each city from 1 to 3, then the chromosomes/permutations can be expressed as these indices in different order like so: Chromosome 1 = [1,2,3], Chromosome 2 = [2,1,3] and so on.

Gene – Particular city in a set of cities. So a chromosome is composed of genes.

Fitness – Fitness is a measure that shows how strong/fit the chromosome is. For TSP the fitness can be simply the distance that is passed when all of the cities are visited. Shorter the distance, fitter the chromosome.

Crossover operation – Crossover is a process of taking some of the genes from one chromosome and the leftover genes from another. These genes are combined together to form a new chromosome

<img width="300" height="209" alt="image" src="https://github.com/user-attachments/assets/d1de552b-d91d-456c-a539-741c7b3abc29" />

The demo program uses the simple approach of picking one parent index from the top/best half of the population and the other parent index from the other half of the population. The next challenge is to combine half of the first parent with half of the second parent in a way that keeps characteristics of both parents but generates a legal solution where each city appears only once.parent1 = (7, 0, 3, 8, 5, 4, 6, 2, 1, 9) and parent2 = (9, 0, 7, 3, 8, 2, 5, 4, 6, 1). If you naively take the left half of parent1 and the right half of parent2 you get a child of (7, 0, 3, 8, 5, 2, 5, 4, 6, 1), which isn't a legal solution because the child has two 5 values and no 9 value.

The crossover point is the middle value in each parent. There are many more complicated ways to implement crossover, but the simple approach used in the demo is usually effective in my experience.

Mutation operation – Mutation is a process where some of the genes in a chromosome are altered. For example the index of two genes are swapped.

<img width="531" height="691" alt="image" src="https://github.com/user-attachments/assets/a3224eda-bc90-4b94-baed-0a66d7f51fa7" />

Choosing the initial population.
Initial population is an arbitrary (chosen by the user) amount of chromosomes that are generated randomly. Each chromosome has same genes but in different and in this case, random order.
Calculate fitness
Fitness of each gene is calculated and stored into an array.
Select the fittest chromosomes
The user specified number of fittest chromosomes are selected, leftover chromosomes are deleted.
Create new generation
A new generation of chromosomes (children) are generated from the fittest chromosomes of the previous generation (parent generation). Crossover and mutation operations are used on randomly selected parents from the parent generation. Fitter the parent is, the better are the chances to be selected for breeding. Also before creation of each children, crossover or mutation process is selected with user specified probability.
Repeat steps 2 to 4 until end generation is reached
Return the fittest chromosome


