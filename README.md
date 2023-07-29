# GIT_Solver
 The software is a basic machine learning classification tool written in java. During training only samples of one class are set as target. Backprogagation aims at increasing the multidimensional Euclidian distance of non-target samples and increasing the accuracy ('score' in plot).
 
![Solver](https://github.com/dsandersGit/GIT_Solver/assets/140900940/56289dc7-098a-40fe-9288-9b2d615bae5e)

Looping through cycles for all classes multiple classification models are created and combined to an ensemble of models. 
During training, model's approximation to accuracy is plotted and validation is posible when dataset is splitted into train- and test- subsets. 

![Solver_Validation](https://github.com/dsandersGit/GIT_Solver/assets/140900940/d7b10f1d-c08c-4fc6-a79a-1d5e61b91944)

Applying an ensemble to a dataset sample's estimated classification is obtained. Ensemble can be saved for further application on unclassified data, if respective data structure is given.

![Solver_Classification](https://github.com/dsandersGit/GIT_Solver/assets/140900940/aa770ff8-1fde-4a6d-aaaf-5d8288b57cc4)

Status of the tool is 'under development', yet capable of running. 
Datasets in CSV, exhibiting numerical values and one String classification, can be processed. Header is optional. Exemplarily the first data of the IRIS dataset, which is included, are formatted like:

Sepal_Length,Sepal_Width,Petal_Length,Petal_Width,Species

5.1,3.5,1.4,0.2,Iris-setosa

4.9,3.0,1.4,0.2,Iris-setosa

4.7,3.2,1.3,0.2,Iris-setosa

....

Datasets with up to approx. 5000 samples and 100 variables are processed within reasonable speed on a general computer. 
