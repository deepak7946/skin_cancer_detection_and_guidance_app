# -*- coding: utf-8 -*-
"""InceptionV3_Model_with_Test_prediction.ipynb

Automatically generated by Colaboratory.

Original file is located at
    https://colab.research.google.com/drive/1ujH3egzQuDAWwv9baVbbXzBF1MaqzsTw
"""

from google.colab import drive
drive.mount('/content/drive')

#from tensorflow.compat.v1 import ConfigProto
#from tensorflow.compat.v1 import InteractiveSession

#config = ConfigProto()
#config.gpu_options.per_process_gpu_memory_fraction = 0.5
#config.gpu_options.allow_growth = True
#session = InteractiveSession(config=config)

# import the libraries as shown below

from tensorflow.keras.layers import Input, Lambda, Dense, Flatten
from tensorflow.keras.models import Model
from tensorflow.keras.applications.inception_v3 import InceptionV3
#from keras.applications.vgg16 import VGG16
from tensorflow.keras.applications.inception_v3 import preprocess_input
from tensorflow.keras.preprocessing import image
from tensorflow.keras.preprocessing.image import ImageDataGenerator,load_img
from tensorflow.keras.models import Sequential
import numpy as np
from glob import glob
#import matplotlib.pyplot as plt

# re-size all the images to this
IMAGE_SIZE = [224, 224]

train_path = '/content/drive/My Drive/train_dir'
valid_path = '/content/drive/My Drive/base_dir/val_dir'
test_path = '/content/drive/My Drive/base_dir/val_dir'

# Import the Vgg 16 library as shown below and add preprocessing layer to the front of VGG
# Here we will be using imagenet weights

inception = InceptionV3(input_shape=IMAGE_SIZE + [3], weights='imagenet', include_top=False)

# don't train existing weights
for layer in inception.layers:
    layer.trainable = False

# useful for getting number of output classes
folders = glob('/content/drive/My Drive/train_dir/*')

len(folders)

# our layers - you can add more if you want
x = Flatten()(inception.output)

prediction = Dense(len(folders), activation='softmax')(x)

# create a model object
model = Model(inputs=inception.input, outputs=prediction)

# view the structure of the model
model.summary()

# tell the model what cost and optimization method to use
model.compile(
  loss='categorical_crossentropy',
  optimizer='adam',
  metrics=['accuracy']
)

# Use the Image Data Generator to import the images from the dataset
from tensorflow.keras.preprocessing.image import ImageDataGenerator

train_datagen = ImageDataGenerator(rescale = 1./255,
                                   shear_range = 0.2,
                                   zoom_range = 0.2,
                                   horizontal_flip = True)

test_datagen = ImageDataGenerator(rescale = 1./255)

# Make sure you provide the same target size as initialied for the image size
training_set = train_datagen.flow_from_directory('/content/drive/My Drive/train_dir',
                                                 target_size = (224, 224),
                                                 batch_size = 400,
                                                 class_mode = 'categorical')

# Note: shuffle=False causes the test dataset to not be shuffled
test_set = test_datagen.flow_from_directory('/content/drive/My Drive/base_dir/val_dir',
                                            target_size = (224, 224),
                                            batch_size = 50,shuffle=False)

val_set = test_datagen.flow_from_directory('/content/drive/My Drive/base_dir/val_dir',
                                            target_size = (224, 224),
                                            batch_size = 50,
                                            class_mode = 'categorical')

# fit the model
# Run the cell. It will take some time to execute
r = model.fit_generator(
  training_set,
  validation_data=val_set,
  epochs=20,
  steps_per_epoch=len(training_set),
  validation_steps=len(val_set)
)

# save it as a h5 file


from tensorflow.keras.models import load_model

model.save('model_inceptionv3.h5') # saving the model data in h5 format

from keras.models import load_model
model = load_model('model_inceptionv3.h5') # loading the model for evaluation 

from google.colab import files
files.download('model_inceptionv3.h5')

import matplotlib.pyplot as plt

# plot the loss
plt.plot(r.history['loss'], label='train loss')
plt.plot(r.history['val_loss'], label='val loss')
plt.legend()
plt.show()
plt.savefig('LossVal_loss')

# plot the accuracy
plt.plot(r.history['accuracy'], label='train acc')
plt.plot(r.history['val_accuracy'], label='val acc')
plt.legend()
plt.show()
plt.savefig('AccVal_acc')

y_pred = model.predict(test_set) # calculating the prediction probability

y_pred # printing the prediction values

import numpy as np
y_pred = np.argmax(y_pred, axis=1) # finding the index value of y_pred with maximum probability value across the axis =1

y_pred # printing the prediction values