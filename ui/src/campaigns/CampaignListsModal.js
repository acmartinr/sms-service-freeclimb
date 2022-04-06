import React, {Component} from 'react';

import './CampaignListsModal.css';

import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import TablePagination from '@material-ui/core/TablePagination';

import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';

import CampaignsAPI from '../campaigns/CampaignsAPI';
import Session from '../common/Session'

import Checkbox from '@material-ui/core/Checkbox';

class CampaignListsModal extends Component {
  constructor(props) {
    super(props);

    this.state = {
      id: props.selectedCampaign.id,
      title: 'Update Campaign Phone Lists',
      submitButtonLabel: 'Update',
      lists: [],
      selectedLists: [],
      count: 0,
      rowsPerPage: 10,
      page: 0
    }

    this.handleChangePage = this.handleChangePage.bind(this);
    this.handleChangeRowsPerPage = this.handleChangeRowsPerPage.bind(this);
    this.updateLists = this.updateLists.bind(this);
    this.updateSelectedLists = this.updateSelectedLists.bind(this);
    this.selectedListsContainList = this.selectedListsContainList.bind(this);
  }

  componentDidMount() {
    this.updateSelectedLists();
  }

  handleChangePage(event, value) {
    this.setState({page: value}, this.updateLists);
  }

  handleChangeRowsPerPage(event) {
    this.setState({rowsPerPage: event.target.value}, this.updateLists);
  }

  updateLists() {
    const request = {
      userId: Session.getUser().id,
      page: this.state.page,
      limit: this.state.rowsPerPage
    };

    CampaignsAPI.getLists(request,
    response => {
      if (response.status === "OK") {
        const lists = response.data.lists;
        for (var i = 0; i < lists.length; i++) {
          lists[i].selected = false;
          for (var j = 0; j < this.state.selectedLists.length; j++) {
            if (lists[i].id === this.state.selectedLists[j].id) {
              lists[i].selected = true;
              break;
            }
          }
        }

        this.setState({
          lists: lists,
          count: response.data.count
        });
      }
    });
  }

  updateSelectedLists() {
    const request = {
      id: this.props.selectedCampaign.id
    };

    CampaignsAPI.getListsForCampaign(request,
    response => {
      if (response.status === "OK") {
        this.setState({
          selectedLists: response.data
        });
        this.updateLists();
      }
    });
  }

  commitListsChanges() {
    const campaign = {
      id: this.props.selectedCampaign.id,
      userId: Session.getUser().id,
      lists: this.state.selectedLists
    }

    CampaignsAPI.updateListsForCampaign(campaign,
    response => {
      if (response.status === 'OK') {
        this.props.handleClose(true);
      }
    });
  }

  selectedListsContainList(list) {
    for (var i = 0; i < this.state.selectedLists.length; i++) {
      if (this.state.selectedLists[i].id === list.id) {
        return true;
      }
    }

    return false;
  }

  onCheckChanged(event, list) {
    const value = event.target.checked;

    var selectedLists = this.state.selectedLists.slice();
    if (value) {
      selectedLists.push(list);
    } else {
      selectedLists.splice(this.getListIndex(list, selectedLists), 1);
    }

    this.setState({'selectedLists': selectedLists});
  };

  getListIndex(list, selectedLists) {
    for (var i = 0; i < selectedLists.length; i++) {
      if (selectedLists[i].id === list.id) {
        return i;
      }
    }

    return -1;
  }

  prettyDate(dateMS) {
    if (dateMS > 0) {
      const date = new Date(dateMS);
      return date.toLocaleDateString(date) + ' ' + date.toLocaleTimeString(date);
    } else {
      return 'empty';
    }
  }

  calculateLeftPhonesCount(row) {
    var result = row.cnt - row.sentCount - row.ignored - row.dnc - row.errors;
    if (result < 0) { result = 0; }

    return result;
  }

  render() {
    return (
      <Dialog
        open
        onClose={this.props.handleClose}
        aria-labelledby="form-dialog-title"
        className="custom-lists-dialog">

        <DialogTitle
          className="padding-bottom-0"
          id="form-dialog-title">{this.state.title}</DialogTitle>

        <DialogContent>
          <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell className='checkbox-cell'/>
                  <TableCell>Name</TableCell>
                  <TableCell>Count</TableCell>
                  <TableCell>Ignored</TableCell>
                  <TableCell>DNC</TableCell>
                  <TableCell>Errors</TableCell>
                  <TableCell>Date</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {this.state.lists.map(row => (
                  <TableRow key={row.id}>
                    <TableCell>
                      <Checkbox
                        color="primary"
                        checked={this.selectedListsContainList(row)}
                        onChange={(e) => this.onCheckChanged(e, row)}/>
                    </TableCell>
                    <TableCell>{row.name}</TableCell>
                    <TableCell>{this.calculateLeftPhonesCount(row)}</TableCell>
                    <TableCell>{row.ignored}</TableCell>
                    <TableCell>{row.dnc}</TableCell>
                    <TableCell>{row.errors}</TableCell>
                    <TableCell>{this.prettyDate(row.date)}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>

            <TablePagination
              rowsPerPageOptions={[10, 25, 50]}
              component="div"
              count={this.state.count}
              rowsPerPage={this.state.rowsPerPage}
              page={this.state.page}
              backIconButtonProps={{
                'aria-label': 'Previous Page',
              }}
              nextIconButtonProps={{
                'aria-label': 'Next Page',
              }}
              onChangePage={this.handleChangePage}
              onChangeRowsPerPage={this.handleChangeRowsPerPage}
            />

        </DialogContent>

        <DialogActions>
          <Button onClick={(e) => this.props.handleClose()} color="primary">
            Cancel
          </Button>
          <Button onClick={(e) => this.commitListsChanges()} color="primary">
            {this.state.submitButtonLabel}
          </Button>
        </DialogActions>
      </Dialog>
    )
  }
}

export default CampaignListsModal;
