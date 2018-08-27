from setuptools import setup
import os
import re
import subprocess

def read_version(pom):
    with open(pom) as f:
        context = f.read()
        version = re.search('<version>(.*?)</version>', context).group()
        return version[9:-10]

root_dir = os.path.dirname(os.path.abspath(__file__))
lib_dir = os.path.join(root_dir, 'pyverdict', 'lib')
pom_path = os.path.join(root_dir, '..', 'pom.xml')
version = read_version(pom_path)
jar_name = 'verdictdb-core-' + version + 'jar-with-dependencies.jar'

os.chdir('..')
subprocess.check_call(['mvn','-DskipTests','-DtestPhase=false','-DpackagePhase=true','clean','package'])
subprocess.check_call(['rm', '-rf', os.path.join(lib_dir, '*')])
subprocess.check_call(['cp', os.path.join('target', jar_name), lib_dir])
os.chdir(root_dir)

setup(name='pyverdict',
      version=version,
      description='Python connector for VerdictDB',
      url='http://verdictdb.org',
      author='Barzan Mozafari, Yongjoo Park',
      author_email='mozafari@umich.edu, pyongjoo@umich.edu',
      license='Apache License, Version 2.0',
      packages=setuptools.find_packages(),
      package_data={'pyverdict': ['lib/*.jar']},
      include_package_data=True
 )