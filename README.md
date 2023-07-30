# GIT_Solver
 The software is a basic machine learning classification tool written in java. During training only samples of one class are set as target. Backpropagation aims at increasing the multidimensional Euclidian distance of non-target samples and increasing the accuracy ('score' in plot).
 
![Solver](https://github.com/dsandersGit/GIT_Solver/assets/140900940/16945d0b-ccca-4f52-b66f-455cec2bd1e2)


Looping through cycles for all classes multiple classification models are created and combined to an ensemble of models. 
During training, model's approximation to accuracy is plotted and validation is posible when dataset is splitted into train- and test- subsets. 

![Solver_Validation](https://github.com/dsandersGit/GIT_Solver/assets/140900940/1dea8a4b-45b7-4aba-8406-2fad58b1f0cc)


Applying an ensemble to a dataset sample's estimated classification is obtained. Ensemble can be saved for further application on unclassified data, if respective data structure is given.

![Solver_Classification](https://github.com/dsandersGit/GIT_Solver/assets/140900940/78912c34-c7df-474c-a465-48f82b564321)


Status of the tool is 'under development', yet capable of running. 
Datasets in CSV, exhibiting numerical values and one String classification, can be processed. Header is optional. Exemplarily the first data of the IRIS dataset, which is included, are formatted like:

Sepal_Length,Sepal_Width,Petal_Length,Petal_Width,Species

5.1,3.5,1.4,0.2,Iris-setosa

4.9,3.0,1.4,0.2,Iris-setosa

4.7,3.2,1.3,0.2,Iris-setosa

....

Datasets with up to approx. 5000 samples and 100 variables are processed within reasonable speed on a general computer. 
