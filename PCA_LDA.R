library(MASS)
require(MASS)

options(max.print=999999)
numPC = 5
limitPcNum = 7
lowPcNum = 3
#>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
# Function to save data as CSV
write.excel <- function(x,row.names=TRUE,col.names=TRUE) {
  write.table(x,"clipboard",sep="\t",row.names=row.names,col.names=col.names)
}
#<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

f <- paste0("D:\\data.csv") 
if (file.exists(f)){
   data <- read.csv(f,header=T, sep=",")
}else{
   data <- read.csv("D:\\data.csv.bak",header=T, sep=",")                                               # Reads data table from file
}
#data <- read.csv("D://data.csv",header=T, sep=",")                                                     # Reads data table from file

data.label <- data$sample                                                                               # First column is sample name
data.cls <- as.factor(data$class)                                                                       # Second column is sample class > set as factor
data <- data[, 3:ncol(data)]                                                                            # Omit frist two columns to create dataset for analysis

data.pca = prcomp(data,center = TRUE, scale. = FALSE)                                                  # run PCA alternative
#data.pca = prcomp(data)                                                                                 # run PCA 
data.pca.var = data.pca$sdev^2                                                                          # calculate variance
data.pca.ve <- data.pca.var/sum(data.pca.var)                                                           # calculate variance

# Test for significant variance: Must be over 5%
data.pca.var = data.pca$sdev^2                                                                          # check variance
data.pca.ve <- data.pca.var/sum(data.pca.var)                                                           # check variance
for (i in 1:length(data.pca.ve)) {                                                                      # loops until variance <= 5%
   if ( data.pca.ve[i] > 0.05){ numPC <- i}
}                                                                                                       # loop end
PCA_Dims <-numPC                                                              # DEFAULT: num of PC's equal to num variables
if ( PCA_Dims >= numPC) {PCA_Dims = numPC}                                                                   # if 5% variance num < DEFAULT then 5% variance
if (PCA_Dims > limitPcNum) PCA_Dims <- limitPcNum                                                           # LIMIT max num PC's
if (PCA_Dims < lowPcNum) PCA_Dims <- lowPcNum                                                               # LIMIT min num PC's
if (PCA_Dims > length(data.pca$rotation[,1])) PCA_Dims <-length(data.pca$rotation[,1])                      # Falls min < numAreas
print(PCA_Dims)

pcdata = data.frame(data.pca$x[,1:PCA_Dims],Class=data.cls)                                               # data restructuring for LDA analysis

numClasses <- length(levels(data.cls))                                                                  # LDA nur wenn genug Klassen
if ( numClasses > 1){
  pc_lda <- lda( Class ~ .,data=pcdata)                                                                   # run LDA
  prop_pc_lda = pc_lda$svd^2/sum(pc_lda$svd^2)
  pc_plda <- predict(object = pc_lda,newdata = pcdata)
  dataset = data.frame(data.cls,pc_plda$x)
  numLDA = length(prop_pc_lda[1:PCA_Dims])
}
# >>>>>>>>>>>>>>>>>>>>>>> OUTPUT SECTION: DO NOT EDIT BELOW >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
#o<-paste(sep=":", "DIM_PC", numPC,length(data.pca$rotation[1,]))
o<-paste(sep=":", "DIM_PC", PCA_Dims,length(data.pca$rotation[,1]))

print(noquote(o))                                                                                       # PCA Dimension
                                                                                    
#PCA
#Rotation = Eigenvectors
for (j in 1:length(data.pca$rotation[,1])){
  for (i in 1:PCA_Dims){
    o <- paste(sep=":","rotation",j,i,data.pca$rotation[j,i ])
    print(noquote(o))
  }
}
# PCA Variances
for (i in 1:PCA_Dims){
  o <- paste(sep=":","PCA_variance",i,data.pca.ve[i])
  print(noquote(o))
}
#print("<pc_scores>"); head(data.pca$x[,1:lenPCA ],nrow(data) );print("</pc_scores>")                    # P_Komponenten PCA[][]
#LDA
#if ( lenPCA > length(prop_pc_lda))  lenPCA = length(prop_pc_lda)
#dim(pc_lda$scaling)[1]
#dim(pc_lda$scaling)[2]

if ( numClasses > 1){
  numLDA = dim(pc_lda$scaling)[2]
  numPC = dim(pc_lda$scaling)[1]
  
  o<-paste(sep=":","DIM_LDA",numLDA ,numPC)
  print(noquote(o))  
  for (i in 1:numLDA){
    o <- paste(sep=":","LDA_variance",i,prop_pc_lda[i])
    print(noquote(o))
  }
  
  # Scaling = EV LD1/PC1,LD2/PC2,LD3/PC3...
  for (j in 1:numPC){
    for (i in 1:numLDA){
      o <- paste(sep=":","scaling",j,i,pc_lda$scaling[j,i ])
      print(noquote(o))
    }
  }
}
print("<ENDE>")
##Test

# <<<<<<<<<<<<<<<<<<<<<<< OUTPUT SECTION: DO NOT EDIT ABOVE <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<