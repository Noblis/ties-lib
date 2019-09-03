#!/usr/bin/env python

################################################################################
# Copyright 2019 Noblis, Inc                                                   #
#                                                                              #
# Licensed under the Apache License, Version 2.0 (the "License");              #
# you may not use this file except in compliance with the License.             #
# You may obtain a copy of the License at                                      #
#                                                                              #
#    http://www.apache.org/licenses/LICENSE-2.0                                #
#                                                                              #
# Unless required by applicable law or agreed to in writing, software          #
# distributed under the License is distributed on an "AS IS" BASIS,            #
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.     #
# See the License for the specific language governing permissions and          #
# limitations under the License.                                               #
################################################################################

from io import open
from os import path
from setuptools import find_packages, setup


here = path.abspath(path.dirname(__file__))


# Get the long description from the README file
def long_description():
    with open(path.join(here, 'README.rst'), encoding='utf-8') as f:
        return f.read()


# Get dependencies from requirements.txt file
def load_dependencies():
    with open("requirements.txt") as f:
        return [dep for dep in [s.strip() for s in f.readlines()] if dep and not dep.startswith("#")]


setup(name="ties-lib",
      version="0.9.1",
      description="Triage Import Export Schema (TIES)",
      long_description=long_description(),
      license="Apache License, Version 2.0",
      url="https://github.com/Noblis/ties-lib",
      author="Zack Hutzell",
      author_email="zack.hutzell@noblis.org",
      classifiers=[
          "Development Status :: 4 - Beta",
          "Intended Audience :: Developers",
          "Topic :: Software Development :: Libraries",
          "License :: OSI Approved :: Apache Software License",
          "Programming Language :: Python :: 2",
          "Programming Language :: Python :: 2.7",
          "Programming Language :: Python :: 3",
          "Programming Language :: Python :: 3.4",
          "Programming Language :: Python :: 3.5",
          "Programming Language :: Python :: 3.6",
          "Programming Language :: Python :: 3.7",
      ],
      packages=find_packages(exclude=["ties.test", "ties.cli.test"]),
      include_package_data=True,
      entry_points={
          "console_scripts": [
              "ties-convert=ties.cli.ties_convert:main",
              "ties-format=ties.cli.ties_format:main",
              "ties-validate=ties.cli.ties_validate:main"
          ]
      },
      python_requires=">=2.7, !=3.0.*, !=3.1.*, !=3.2.*, !=3.3.*",
      install_requires=load_dependencies())
